package com.github.project3.controller.reply;

import com.github.project3.dto.reply.ReplyRequest;
import com.github.project3.dto.reply.ReplyResponse;
import com.github.project3.dto.reply.UpdateReplyRequest;
import com.github.project3.service.reply.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping("/{reviewId}/{userId}")
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Integer reviewId,
            @PathVariable Integer userId,
            @RequestBody ReplyRequest replyRequest) {
        ReplyResponse replyResponse = replyService.createReply(userId, reviewId, replyRequest);
        return ResponseEntity.status(201).body(replyResponse);
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<ReplyResponse>> getRepliesByReviewId(
            @PathVariable Integer reviewId) {
        List<ReplyResponse> replies = replyService.getRepliesByReviewId(reviewId);
        return ResponseEntity.status(200).body(replies);
    }

    @PutMapping("/{replyId}/{userId}")
    public ResponseEntity<ReplyResponse> updateReply(
            @PathVariable Integer replyId,
            @PathVariable Integer userId,
            @RequestBody UpdateReplyRequest updateReplyRequest) {
        ReplyResponse updatedReply = replyService.updateReply(userId, replyId, updateReplyRequest.getComment());
        return ResponseEntity.status(200).body(updatedReply);
    }

    @DeleteMapping("/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Integer replyId) {
        replyService.deleteReply(replyId);
        return ResponseEntity.status(204).build();
    }
}
