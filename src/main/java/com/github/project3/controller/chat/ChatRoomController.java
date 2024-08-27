package com.github.project3.controller.chat;

import com.github.project3.dto.chat.ChatRoomResponse;
import com.github.project3.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatRooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> createChatRoom(@RequestParam String chatRoomName, @PathVariable Integer userId) {
        chatRoomService.createChatRoom(chatRoomName, userId);
        return ResponseEntity.ok("새로운 채팅방이 생성되었습니다.");
    }

    // 채팅방 목록 조회
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms() {
        List<ChatRoomResponse> chatRooms = chatRoomService.getAllChatRooms();
        return ResponseEntity.ok(chatRooms);
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