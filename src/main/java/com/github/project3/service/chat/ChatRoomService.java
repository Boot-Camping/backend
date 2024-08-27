package com.github.project3.service.chat;

import com.github.project3.dto.chat.ChatRoomResponse;
import com.github.project3.entity.chat.ChatRoomEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.repository.chat.ChatRoomRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    public void createChatRoom(String chatRoomName, Integer userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setName(chatRoomName);
        chatRoom.setCreatedBy(user);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomResponse> getAllChatRooms() {
        List<ChatRoomEntity> chatRooms = chatRoomRepository.findAll();

        List<ChatRoomResponse> response = chatRooms.stream()
                .map(chatRoom -> ChatRoomResponse.of(
                        chatRoom.getId(),
                        chatRoom.getName(),
                        chatRoom.getCreatedBy().getLoginId(),
                        chatRoom.getJoinedBy() != null ? chatRoom.getJoinedBy().getLoginId() : null,
                        chatRoom.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return response;
    }

//    public ChatRoomDTO getChatRoom(Long id) {
//    }
}
