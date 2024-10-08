package com.github.project3.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.project3.dto.chat.MessageRequest;
import com.github.project3.dto.chat.MessageResponse;
import com.github.project3.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    // 각 채팅방별 세션 관리 (채팅방 ID -> 세션 리스트)
    private Map<Integer, List<WebSocketSession>> chatRoomSessions = new HashMap<>();

    // 사용자 ID와 세션 간의 매핑
    private Map<String, Integer> sessionToUserId = new HashMap<>();

    // 각 세션별 Pong 수신 실패 횟수
    private Map<String, Integer> pongMissCount = new HashMap<>();

    private static final long PING_INTERVAL = 30000; // Ping 전송 간격 (30초마다 핑 전송)
    private static final int MAX_PONG_MISS_COUNT = 3; // 최대 Pong 수신 실패 허용 횟수

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결이 설정되었습니다. 세션 ID: " + session.getId());

        // 쿼리 파라미터에서 userId와 chatRoomId 가져오기
        String query = session.getUri().getQuery();
        Map<String, String> queryParams = splitQuery(query);

        // 쿼리 파라미터에서 userId와 chatRoomId가 없다면 에러 처리
        if (!queryParams.containsKey("userId") || !queryParams.containsKey("chatId")) {
            log.error("userId 또는 chatId가 쿼리 파라미터에 포함되어 있지 않습니다.");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // userId와 chatRoomId를 Integer 로 변환
        Integer userId = Integer.valueOf(queryParams.get("userId"));
        Integer chatRoomId = Integer.valueOf(queryParams.get("chatId"));

        // 세션과 userId 매핑
        sessionToUserId.put(session.getId(), userId);

        // 세션의 초기 Pong 수신 실패 횟수 설정
        pongMissCount.put(session.getId(), 0);

        // 채팅방에 세션 등록
        registerSession(chatRoomId, session, userId);

        // 기존 메시지 로드 및 전송
        loadAndSendPreviousMessages(session, chatRoomId);
    }

    private void loadAndSendPreviousMessages(WebSocketSession session, Integer chatRoomId) {
        try {
            // 채팅방의 기존 메시지 로드
            List<MessageResponse> previousMessages = messageService.getMessagesByChatRoom(chatRoomId);

            for (MessageResponse message : previousMessages) {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
            }

        } catch (Exception e) {
            log.error("기존 메시지를 로드하는 중 오류가 발생했습니다.", e);
            try {
                session.sendMessage(new TextMessage("기존 메시지를 로드하는 중 오류가 발생했습니다."));
            } catch (Exception sendException) {
                log.error("오류 메시지를 전송하는 중 오류가 발생했습니다.", sendException);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String payload = textMessage.getPayload();
        log.info("메세지를 수신했습니다: " + payload);

        try {
            // 1. 메세지 파싱
            MessageRequest messageRequest = objectMapper.readValue(payload, MessageRequest.class);
            Integer chatRoomId = messageRequest.getChatRoomId();

            Integer senderId = sessionToUserId.get(session.getId());
            if (senderId == null) {
                session.sendMessage(new TextMessage("사용자 정보가 없습니다. 세션이 인증되지 않았습니다."));
                return;
            }

            // 2. 메세지 저장
            MessageResponse messageResponse = messageService.sendMessage(messageRequest, senderId);

            // 3. 세션 등록
            registerSession(chatRoomId, session, senderId);

            // 4. 메세지 브로드캐스트
            broadcastMessageToChatRoom(chatRoomId, messageResponse);

        } catch (Exception e) {
            log.error("메세지 처리 중 오류가 발생했습니다.", e);
            session.sendMessage(new TextMessage("오류가 발생했습니다. 메세지를 처리할 수 없습니다."));
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("Pong 메시지를 수신했습니다. 세션 ID: " + session.getId());

        // Pong 메시지를 수신하면 실패 횟수를 0으로 초기화
        pongMissCount.put(session.getId(), 0);
    }

    @Scheduled(fixedRate = PING_INTERVAL)
    private void sendPingMessages() {
        chatRoomSessions.values().forEach(sessions -> sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new PingMessage(ByteBuffer.wrap("ping".getBytes())));
                    log.info("Ping 메시지를 보냈습니다. 세션 ID: " + session.getId());

                    // Ping 을 보낸 후, 일정 시간 내에 Pong 을 받지 못하면 카운트 1 증가
                    int currentMissCount = pongMissCount.getOrDefault(session.getId(), 0);
                    pongMissCount.put(session.getId(), currentMissCount + 1);

                    // Pong 수신 실패 횟수가 MAX_PONG_MISS_COUNT 를 초과하면 세션을 닫음
                    if (pongMissCount.get(session.getId()) >= MAX_PONG_MISS_COUNT) {
                        log.warn("Pong 메시지를 3번 수신하지 못했습니다. 세션을 닫습니다: " + session.getId());
                        session.close(CloseStatus.GOING_AWAY);
                        unregisterSession(session);
                    }
                }
            } catch (Exception e) {
                log.error("Ping 메시지 전송 중 오류 발생: ", e);
            }
        }));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 닫혔을 때 세션 관리
        unregisterSession(session);
        log.info("WebSocket 연결이 닫혔습니다. 세션 ID: " + session.getId());
    }

    private void registerSession(Integer chatRoomId, WebSocketSession session, Integer userId) {
        // 세션을 특정 채팅방에 등록
        List<WebSocketSession> sessions = chatRoomSessions.computeIfAbsent(chatRoomId, k -> new ArrayList<>());
        if (!sessions.contains(session)) {  // 중복된 세션이 등록되지 않도록 체크
            sessions.add(session);
        }
        sessionToUserId.put(session.getId(), userId);  // 사용자 ID와 세션을 매핑
    }

    private void unregisterSession(WebSocketSession session) {
        // 세션을 제거하고 관련된 사용자 매핑을 제거
        Integer userId = sessionToUserId.remove(session.getId());
        if (userId != null) {
            chatRoomSessions.values().forEach(sessions -> sessions.remove(session));
        }
    }

    private void broadcastMessageToChatRoom(Integer chatRoomId, MessageResponse messageResponse) {
        // 특정 채팅방에 속한 모든 세션에 메세지를 브로드캐스트
        List<WebSocketSession> sessions = chatRoomSessions.get(chatRoomId);
        if (sessions != null) {
            sessions.forEach(session -> {
                try {
                    String messageJson = objectMapper.writeValueAsString(messageResponse);
                    session.sendMessage(new TextMessage(messageJson));
                } catch (Exception e) {
                    log.error("메세지 전송 중 오류 발생: ", e);
                }
            });
        }
    }

    // 쿼리 파라미터로 받은 문자열 분리
    private Map<String, String> splitQuery(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryPairs.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return queryPairs;
    }
}
