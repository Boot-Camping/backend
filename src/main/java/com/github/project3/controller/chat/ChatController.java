package com.github.project3.controller.chat;

import com.github.project3.service.chat.ChatRoomService;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestParam String name, @RequestParam Set<Long> userIds) {
        ChatRoomDTO chatRoom = chatRoomService.createChatRoom(name, userIds);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable Long id) {
        ChatRoomDTO chatRoom = chatRoomService.getChatRoom(id);
        if (chatRoom == null) {
            throw new NotFoundException("채팅방이 존재하지 않습니다.");
        }
        return ResponseEntity.ok(chatRoom);
    }
}