package com.github.project3.controller.mypage;

import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.dto.mypage.UserProfileUpdateImageResponse;
import com.github.project3.dto.mypage.UserProfileUpdatePasswordRequest;
import com.github.project3.dto.mypage.UserProfileUpdateResponse;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.service.mypage.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    // 유저정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<List<UserProfileResponse>> getUserMyPage(@PathVariable Integer id){
        List<UserProfileResponse> userMyPageResponse = userProfileService.getUserMyPage(id);
        return ResponseEntity.ok(userMyPageResponse);
    }
    // 유저 기본정보 수정
    @PutMapping("/{id}")
    public ResponseEntity <String> getUpdateUser(
            @PathVariable Integer id,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String addr){
        userProfileService.getUpdateUser(id, tel, addr);
        return ResponseEntity.ok("유저정보 수정 완료");
    }
    // 유저 이미지 생성/수정
    @PostMapping("/images/{id}")
    public ResponseEntity <String> getUpdateImageUser(
            @PathVariable Integer id,
            @RequestPart("images") MultipartFile images){
        userProfileService.getUpdateImage(id, images);
        return ResponseEntity.ok("유저프로필 수정 완료");
    }
    // 유저 비밀번호 수정
//    @PutMapping("/password/{id}")
//    public ResponseEntity <String> getUpdatePasswordUser(
//            @PathVariable Integer id,
//            @RequestBody UserProfileUpdatePasswordRequest userProfileUpdatePasswordRequest){
//        userProfileService.getUpdatePasswordUser(id, userProfileUpdatePasswordRequest);
//        return new ResponseEntity<>()
//    }
}
