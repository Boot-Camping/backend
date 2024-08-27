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
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody MessageRequest messageRequest) {
        MessageResponse messageResponse = messageService.sendMessage(messageRequest);
        return ResponseEntity.ok(messageResponse);
    }

}