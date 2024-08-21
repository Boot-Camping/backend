package com.github.project3.repository.reply;

import com.github.project3.entity.reply.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {
    List<ReplyEntity> findByReviewId(Integer reviewId);
}
