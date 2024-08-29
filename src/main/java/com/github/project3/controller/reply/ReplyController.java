package com.github.project3.controller.reply;

import com.github.project3.dto.reply.ReplyRequest;
import com.github.project3.dto.reply.ReplyResponse;
import com.github.project3.dto.reply.UpdateReplyRequest;
import com.github.project3.service.reply.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/replies")
public class ReplyController {
    private final ReplyService replyService;

    /**
     * 사용자가 특정 리뷰에 대한 댓글을 작성합니다.
     *
     * @param reviewId   댓글을 작성할 리뷰의 고유 ID
     * @param replyRequest 댓글 작성 요청 정보가 담긴 DTO 객체
     * @return 작성된 댓글에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @Operation(summary = "댓글 작성", description = "사용자가 특정 리뷰에 대한 댓글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Integer reviewId,
            @RequestBody ReplyRequest replyRequest) {
        ReplyResponse replyResponse = replyService.createReply(replyRequest.getUserId(), reviewId, replyRequest);
        return ResponseEntity.status(201).body(replyResponse);
    }

    /**
     * 특정 리뷰에 달린 댓글들을 조회합니다.
     *
     * @param reviewId  댓글들을 조회할 리뷰의 고유 ID
     * @return 해당 리뷰에 달린 댓글 목록을 포함한 ResponseEntity 객체
     */
    @Operation(summary = "리뷰별 댓글 조회", description = "특정 리뷰에 달린 댓글들을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReplyResponse>> getRepliesByReviewId(
            @PathVariable Integer reviewId) {
        List<ReplyResponse> replies = replyService.getRepliesByReviewId(reviewId);
        return ResponseEntity.ok(replies);
    }

    /**
     * 사용자가 작성한 댓글을 수정합니다.
     *
     * @param reviewId           댓글이 속한 리뷰의 고유 ID
     * @param replyId           수정하려는 댓글의 고유 ID
     * @param updateReplyRequest 댓글 수정 요청 정보가 담긴 DTO 객체
     * @return 수정된 댓글에 대한 응답 정보를 포함한 ResponseEntity 객체
     */
    @Operation(summary = "댓글 수정", description = "사용자가 작성한 댓글을 수정합니다.")
    @PutMapping("/{replyId}")
    public ResponseEntity<ReplyResponse> updateReply(
            @PathVariable Integer reviewId,
            @PathVariable Integer replyId,
            @RequestBody UpdateReplyRequest updateReplyRequest) {
        ReplyResponse updatedReply = replyService.updateReply(updateReplyRequest.getUserId(), replyId, updateReplyRequest.getComment());
        return ResponseEntity.ok(updatedReply);
    }

    /**
     * 사용자가 작성한 댓글을 삭제합니다.
     *
     * @param reviewId  댓글이 속한 리뷰의 고유 ID
     * @param replyId  삭제하려는 댓글의 고유 ID
     * @return 성공적으로 삭제된 경우 HTTP 상태 204(no content)를 반환
     */
    @Operation(summary = "댓글 삭제", description = "사용자가 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Integer reviewId,
            @PathVariable Integer replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
}