package com.github.project3.controller.mypage;

import com.github.project3.dto.mypage.MypageResponse;
import com.github.project3.dto.mypage.MypageUpdatePasswordRequest;
import com.github.project3.dto.mypage.NoticeResponse;
import com.github.project3.service.mypage.MypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofile")
public class MypageController {

    private final MypageService mypageService;
    // 유저정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<List<MypageResponse>> getUserMyPage(@PathVariable Integer id){
        List<MypageResponse> userMyPageResponse = mypageService.getUserMyPage(id);
        return ResponseEntity.ok(userMyPageResponse);
    }
    // 유저 기본정보 수정
    @PutMapping("/{id}")
    public ResponseEntity <String> getUpdateUser(
            @PathVariable Integer id,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String addr){
        mypageService.getUpdateUser(id, tel, addr);
        return ResponseEntity.ok("유저정보 수정 완료");
    }
    // 유저 이미지 생성/수정(+UpdateAt, 업데이트시간)
    @PostMapping("/images/{id}")
    public ResponseEntity <String> getUpdateImageUser(
            @PathVariable Integer id,
            @RequestPart("images") MultipartFile images){
        mypageService.getUpdateImage(id, images);
        return ResponseEntity.ok("유저프로필 수정 완료");
    }
    // 비밀번호 수정(+같은비밀번호 에러처리)
    @PutMapping("/password/{id}")
    public ResponseEntity <String> getUpdatePasswordUser(
            @PathVariable Integer id,
            @Valid @RequestBody MypageUpdatePasswordRequest UpdatePasswordRequest){

        mypageService.getUpdatePasswordUser(id, UpdatePasswordRequest);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
    // 공지사항 조회
    @GetMapping("/notice/all")
    public ResponseEntity<List<NoticeResponse>> getNotice(){
        List<NoticeResponse> notice = MypageService.getNoticeAll();
        return ResponseEntigty.ok(notice);
    }
}
