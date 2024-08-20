package com.github.project3.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.admin.AdminNoticeCheckResponse;
import com.github.project3.dto.admin.AdminNoticeDetailCheckResponse;
import com.github.project3.dto.admin.AdminNoticeRegisterRequest;
import com.github.project3.dto.admin.AdminNoticeUpdateRequest;
import com.github.project3.service.admin.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            @RequestPart(value = "request") String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException{

        AdminNoticeRegisterRequest noticeRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeRegisterRequest.class);
        adminNoticeService.registerNotice(noticeRequest, images);
        return ResponseEntity.ok("공지사항 등록 완료.");
    }
    // 공지사항 전체조회(+ size부분은 전체갯수, 페이지별 갯수로 전환)
    @GetMapping("/notice/all")
    public ResponseEntity <List<AdminNoticeCheckResponse>> getNoticeAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size){
        List<AdminNoticeCheckResponse> noticeResponse = adminNoticeService.getNoticeAll(page, size);
        return ResponseEntity.ok(noticeResponse);
    }
    // 공지사항 상세조회
    @GetMapping("/notice/{id}")
    public ResponseEntity <AdminNoticeDetailCheckResponse> getNoticeDetail(
            @PathVariable Integer id){
        AdminNoticeDetailCheckResponse noticeDetailResponse = adminNoticeService.getNoticeDetail(id);
        return ResponseEntity.ok(noticeDetailResponse);
    }

    // 공지사항 수정(= 공지사항 request는 꼭 입력해야함. 프론트에서: 수정버튼 누르면 기존 제목/내용 부분이 나오게끔 구현)
    @PutMapping("/notice/{id}")
    public ResponseEntity <String> getNoticeDetail(
            @PathVariable Integer id,
            @RequestPart(value = "request") String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {
        AdminNoticeUpdateRequest noticeUpdateRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeUpdateRequest.class);
        adminNoticeService.getUpdateNotice(id, noticeUpdateRequest, images);
        return ResponseEntity.ok("공지사항 수정 완료");
    }
        // 공지사항 삭제
    @DeleteMapping("/notice/{id}")
    public ResponseEntity <String> removeNotice(@PathVariable Integer id){
        adminNoticeService.removeNotice(id);
        return ResponseEntity.ok("공지사항이 삭제 완료.");
    }


}
