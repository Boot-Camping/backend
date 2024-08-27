package com.github.project3.service.chat;

import com.github.project3.entity.chat.ChatRoomEntity;
import com.github.project3.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public void createChatRoom(String chatRoomName) {
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setName(chatRoomName);
        chatRoomRepository.save(chatRoom);
    }

//    public ChatRoomDTO getChatRoom(Long id) {
//    }
}
