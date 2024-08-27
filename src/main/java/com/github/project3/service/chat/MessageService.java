package com.github.project3.service.chat;

import com.github.project3.dto.chat.ChatRoomResponse;
import com.github.project3.dto.chat.MessageRequest;
import com.github.project3.dto.chat.MessageResponse;
import com.github.project3.entity.chat.ChatRoomEntity;
import com.github.project3.entity.chat.MessageEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.repository.chat.ChatRoomRepository;
import com.github.project3.repository.chat.MessageRepository;
import com.github.project3.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 메세지 전송
    public MessageResponse sendMessage(MessageRequest messageRequest) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(messageRequest.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        UserEntity sender = userRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        MessageEntity message = new MessageEntity();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(messageRequest.getContent());

        MessageEntity savedMessage = messageRepository.save(message);

        MessageResponse response = MessageResponse.of(
                savedMessage.getId(),
                savedMessage.getChatRoom().getId(),
                savedMessage.getSender().getId(),
                savedMessage.getSender().getLoginId(),
                savedMessage.getContent(),
                savedMessage.getSentAt()
        );

        return response;
    }
}
