package com.github.project3.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.admin.AdminNoticeCheckResponse;
import com.github.project3.dto.admin.AdminNoticeRegisterRequest;
import com.github.project3.service.admin.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;
    private final ObjectMapper objectMapper;

    // 공지사항 등록
    @PostMapping("/notice")
    public ResponseEntity <String> registerNotice(
            @RequestPart(value="noticeRequest") String noticeRequestJson,
            @RequestPart(value = "noticeImages", required = false) List<MultipartFile> images) throws JsonProcessingException{

        AdminNoticeRegisterRequest noticeRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeRegisterRequest.class);
        adminNoticeService.registerNotice(noticeRequest, images);
        return ResponseEntity.ok("공지사항 등록 완료.");
    }
    // 공지사항 전체조회
    @GetMapping("/notice/all")
    public ResponseEntity <List<AdminNoticeCheckResponse>> getNoticeAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        List<AdminNoticeCheckResponse> noticeResponse = adminNoticeService.getNoticeAll(page, size);
        return ResponseEntity.ok(noticeResponse);
    }
    // 공지사항 상세조회

    // 공지사항 수정

    // 공지사항 삭제


}
