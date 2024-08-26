package com.github.project3.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.project3.dto.admin.*;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.service.admin.AdminNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * AdminNoticeController는 관리자용 공지사항 관리 기능을 처리합니다.
 * 공지사항 등록, 조회, 수정, 삭제와 관련된 작업을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
@Api(tags = "관리자 공지사항 관리") // Swagger Tag (한국어)
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;
    private final ObjectMapper objectMapper;

    /**
     * 새로운 공지사항을 등록합니다.
     *
     * @param token 관리자의 인증 토큰
     * @param noticeRequestJson 공지사항 정보가 담긴 JSON 문자열
     * @param images 공지사항과 함께 업로드할 이미지 목록 (선택 사항)
     * @return 성공 메시지를 포함한 ResponseEntity
     * @throws JsonProcessingException JSON 처리 실패 시 발생
     */
    @ApiOperation(value = "공지사항 등록", notes = "새로운 공지사항을 등록합니다. 이미지를 함께 업로드할 수 있습니다.")
    @PostMapping("/notice")
    public ResponseEntity<String> registerNotice(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestPart(value = "request") String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        AdminNoticeRegisterRequest noticeRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeRegisterRequest.class);
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        adminNoticeService.registerNotice(noticeRequest, images, subToken);
        return ResponseEntity.ok("공지사항 등록 완료.");
    }

    /**
     * 공지사항 목록을 페이지로 조회합니다.
     *
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 페이지당 공지사항 수 (기본값: 3)
     * @return 공지사항 목록이 포함된 ResponseEntity
     */
    @ApiOperation(value = "공지사항 전체 조회", notes = "공지사항 목록을 페이지네이션으로 조회합니다.")
    @GetMapping("/notice/all")
    public ResponseEntity<Page<AdminNoticeCheckResponse>> getNoticeAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        Page<AdminNoticeCheckResponse> noticePage = adminNoticeService.getNoticeAll(page, size);
        return ResponseEntity.ok(noticePage);
    }

    /**
     * 특정 공지사항의 상세 정보를 조회합니다.
     *
     * @param id 조회할 공지사항의 ID
     * @return 공지사항의 상세 정보가 포함된 ResponseEntity
     */
    @ApiOperation(value = "공지사항 상세 조회", notes = "특정 공지사항의 상세 정보를 조회합니다.")
    @GetMapping("/notice/{id}")
    public ResponseEntity<AdminNoticeDetailCheckResponse> getNoticeDetail(@PathVariable Integer id) {
        AdminNoticeDetailCheckResponse noticeDetailResponse = adminNoticeService.getNoticeDetail(id);
        return ResponseEntity.ok(noticeDetailResponse);
    }

    /**
     * 기존 공지사항을 수정합니다.
     *
     * @param id 수정할 공지사항의 ID
     * @param token 관리자의 인증 토큰
     * @param noticeRequestJson 수정할 공지사항 정보가 담긴 JSON 문자열
     * @param images 수정할 이미지 목록 (선택 사항)
     * @return 성공 메시지를 포함한 ResponseEntity
     * @throws JsonProcessingException JSON 처리 실패 시 발생
     */
    @ApiOperation(value = "공지사항 수정", notes = "기존 공지사항을 수정합니다. 이미지를 업데이트할 수 있습니다.")
    @PutMapping("/notice/{id}")
    public ResponseEntity<String> updateNotice(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestPart(value = "request", required = false) String noticeRequestJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws JsonProcessingException {

        AdminNoticeUpdateRequest noticeUpdateRequest = objectMapper.readValue(noticeRequestJson, AdminNoticeUpdateRequest.class);
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        adminNoticeService.getUpdateNotice(id, noticeUpdateRequest, images, subToken);
        return ResponseEntity.ok("공지사항 수정 완료");
    }

    /**
     * 특정 공지사항을 삭제합니다.
     *
     * @param id 삭제할 공지사항의 ID
     * @param token 관리자의 인증 토큰
     * @return 성공 메시지를 포함한 ResponseEntity
     */
    @ApiOperation(value = "공지사항 삭제", notes = "특정 공지사항을 삭제합니다.")
    @DeleteMapping("/notice/{id}")
    public ResponseEntity<String> removeNotice(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        adminNoticeService.removeNotice(id, subToken);
        return ResponseEntity.ok("공지사항 삭제 완료.");
    }

    // 사이트 통계 (매출추이, 유저추이, 예약수, (카테고리별 예약자수))
    @ApiOperation(value = "사이트 통계", notes = "유저추이, 예약추이")
    @GetMapping("/stats")
    public ResponseEntity <AdminDataResponse> getAllData(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token){
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        AdminDataResponse dataResponse = adminNoticeService.getAllData(subToken);
        return ResponseEntity.ok(dataResponse);
    }

    /**
     * 모든 회원 정보를 조회합니다.
     *
     * @param token 관리자의 인증 토큰
     * @return 회원 목록이 포함된 ResponseEntity
     */
    @ApiOperation(value = "회원 전체 조회", notes = "모든 회원 정보를 조회합니다.")
    @GetMapping("/user/all")
    public ResponseEntity<List<AdminUserCheckResponse>> getUserAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        List<AdminUserCheckResponse> userResponse = adminNoticeService.getUserAll(subToken);
        return ResponseEntity.ok(userResponse);
    }

    /**
     * 특정 회원을 블랙리스트에 등록합니다.
     *
     * @param id 블랙리스트에 등록할 회원의 ID
     * @param token 관리자의 인증 토큰
     * @return 성공 메시지를 포함한 ResponseEntity
     */
    @ApiOperation(value = "회원 블랙리스트 등록", notes = "특정 회원을 블랙리스트에 등록합니다.")
    @PutMapping("/user/{id}/blacklist")
    public ResponseEntity<String> getBlacklist(
            @PathVariable Integer id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        String subToken = token;
        if (token.startsWith("Bearer ")) {
            // "Bearer "가 포함되어 있다면 "Bearer "를 제거한 부분을 subToken에 저장
            subToken = token.substring(7);
        }
        adminNoticeService.getBlacklist(id, subToken);
        return ResponseEntity.ok("블랙리스트 등록 완료.");
    }

    // 관리자 통장 업데이트(수동작업)
    @PutMapping("/update-balance")
    public ResponseEntity<String> updateAdminBalance(){
        adminNoticeService.updateAdminBalance();
        return ResponseEntity.ok("관리자 잔고 업데이트 완료.");
    }
}
