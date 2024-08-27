package com.github.project3.repository.chat;

import com.github.project3.entity.chat.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {
}
