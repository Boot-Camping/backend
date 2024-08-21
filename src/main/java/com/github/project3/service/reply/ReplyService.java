package com.github.project3.service.reply;

import com.github.project3.dto.reply.ReplyRequest;
import com.github.project3.dto.reply.ReplyResponse;
import com.github.project3.entity.reply.ReplyEntity;
import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.repository.reply.ReplyRepository;
import com.github.project3.repository.review.ReviewRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    // 댓글 생성
    @Transactional
    public ReplyResponse createReply(Integer userId, Integer reviewId, ReplyRequest replyRequest) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 정보를 찾을 수 없습니다."));
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        ReplyEntity reply = ReplyEntity.builder()
                .user(user)
                .review(review)
                .comment(replyRequest.getComment())
                .build();

        replyRepository.save(reply);

        return ReplyResponse.of(reply.getId(), user.getLoginId(), reply.getComment(), reply.getCreatedAt());

    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<ReplyResponse> getRepliesByReviewId(Integer reviewId) {
        return replyRepository.findByReviewId(reviewId).stream()
                .map(reply -> ReplyResponse.of(reply.getId(), reply.getUser().getLoginId(), reply.getComment(), reply.getCreatedAt()))
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public ReplyResponse updateReply(Integer userId, Integer replyId, String newComment) {
        // 대댓글 엔티티 가져오기
        ReplyEntity existingReply = replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("대댓글을 찾을 수 없습니다."));

        // comment 필드만 업데이트
        ReplyEntity updatedReply = ReplyEntity.builder()
                .id(existingReply.getId())
                .user(existingReply.getUser())
                .review(existingReply.getReview())
                .comment(newComment)
                .createdAt(existingReply.getCreatedAt())
                .build();

        // 대댓글 엔티티 저장
        replyRepository.save(updatedReply);

        // 응답 DTO 생성 후 반환
        return ReplyResponse.of(updatedReply.getId(), updatedReply.getUser().getLoginId(), updatedReply.getComment(), updatedReply.getCreatedAt());
    }

    // 댓글 삭제
    @Transactional
    public void deleteReply(Integer replyId) {
        ReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new NotFoundException("대댓글을 찾을 수 없습니다."));
        replyRepository.delete(reply);
    }
}
