package com.github.project3.repository.chat;

import com.github.project3.entity.chat.ChatRoomEntity;
import com.github.project3.entity.chat.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
    List<MessageEntity> findByChatRoom(ChatRoomEntity chatRoom);
}
