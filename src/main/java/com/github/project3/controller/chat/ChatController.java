//package com.github.project3.controller.chat;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@Slf4j
//public class ChatController {
//
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/messages")
//    public ChatMessage sendMessage(ChatMessage message) {
//        log.info("서버에서 수신한 메시지: " + message.getContent()); // 메시지 로그 출력
//        return message; // 이 메시지를 모든 구독자에게 브로드캐스트
//    }
//}