package com.github.project3.controller.chat;

import com.github.project3.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<String> createChatRoom(@RequestParam String chatRoomName) {
        chatRoomService.createChatRoom(chatRoomName);
        return ResponseEntity.ok("새로운 채팅방이 생성되었습니다.");
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable Long id) {
//        ChatRoomDTO chatRoom = chatRoomService.getChatRoom(id);
//        if (chatRoom == null) {
//            throw new NotFoundException("채팅방이 존재하지 않습니다.");
//        }
//        return ResponseEntity.ok(chatRoom);
//    }
}