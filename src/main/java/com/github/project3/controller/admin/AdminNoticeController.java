package com.github.project3.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.admin.AdminNoticeCheckResponse;
import com.github.project3.dto.admin.AdminNoticeDetailCheckResponse;
import com.github.project3.dto.admin.AdminNoticeRegisterRequest;
import com.github.project3.dto.admin.AdminNoticeUpdateRequest;
import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.service.admin.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    // 공지사항 등록
    @PostMapping("/notice")
    public ResponseEntity <String> registerNotice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestPart(value = "request") String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException{

        AdminNoticeRegisterRequest noticeRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeRegisterRequest.class);
        adminNoticeService.registerNotice(noticeRequest, images, token);
        return ResponseEntity.ok("공지사항 등록 완료.");
    }
    // 공지사항 전체조회(= size부분은 전체갯수->페이지별 갯수로 전환 완료)
    @GetMapping("/notice/all")
    public ResponseEntity<Page<AdminNoticeCheckResponse>> getNoticeAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size){
        Page<AdminNoticeCheckResponse> noticePage = adminNoticeService.getNoticeAll(page, size);
        return ResponseEntity.ok(noticePage);
    }
    // 공지사항 상세조회
    @GetMapping("/notice/{id}")
    public ResponseEntity <AdminNoticeDetailCheckResponse> getNoticeDetail(@PathVariable Integer id){
        AdminNoticeDetailCheckResponse noticeDetailResponse = adminNoticeService.getNoticeDetail(id);
        return ResponseEntity.ok(noticeDetailResponse);
    }

    // 공지사항 수정(= title, description 입력안할시 기존값 추출 완료)
    @PutMapping("/notice/{id}")
    public ResponseEntity <String> getNoticeDetail(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestPart(value = "request", required = false) String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        AdminNoticeUpdateRequest noticeUpdateRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeUpdateRequest.class);
        adminNoticeService.getUpdateNotice(id, noticeUpdateRequest, images, token);
        return ResponseEntity.ok("공지사항 수정 완료");
    }
    // 공지사항 삭제
    @DeleteMapping("/notice/{id}")
    public ResponseEntity <String> removeNotice(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        adminNoticeService.removeNotice(id, token);
        return ResponseEntity.ok("공지사항이 삭제 완료.");
    }
    // 회원 블랙리스트 등록
    @PutMapping("/user/{id}/blacklist")
    public ResponseEntity <String> getBlacklist(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        adminNoticeService.getBlacklist(id, token);
        return ResponseEntity.ok("블랙리스트 등록 완료.");
    }


}
