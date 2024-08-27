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

    // 채팅방 개설
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

    // 채팅방 입장
    @PostMapping("/join/{chatRoomId}/user/{userId}")
    public ResponseEntity<String> joinChatRoom(@PathVariable Integer chatRoomId, @PathVariable Integer userId) {
        boolean joined = chatRoomService.joinChatRoom(chatRoomId, userId);
        if (joined) {
            return ResponseEntity.ok("채팅방에 입장했습니다.");
        } else {
            return ResponseEntity.badRequest().body("채팅방 입장에 실패했습니다.");
        }
    }
}