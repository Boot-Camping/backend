package com.github.project3.controller.chat;

import com.github.project3.dto.chat.MessageRequest;
import com.github.project3.dto.chat.MessageResponse;
import com.github.project3.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 전송
    @PostMapping("/send/user/{userId}")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest messageRequest, @PathVariable Integer userId) {
        MessageResponse messageResponse = messageService.sendMessage(messageRequest, userId);
        return ResponseEntity.ok(messageResponse);
    }

    // 채팅방의 메시지 조회
    @GetMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<List<MessageResponse>> getMessagesByChatRoom(@PathVariable Integer chatRoomId) {
        List<MessageResponse> messages = messageService.getMessagesByChatRoom(chatRoomId);
        return ResponseEntity.ok(messages);
    }

}