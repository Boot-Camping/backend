package com.github.project3.controller.mypage;

import com.github.project3.dto.mypage.*;
import com.github.project3.entity.camp.CampEntity;
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
    // 유저 이미지 생성/수정(+UpdateAt)
    @PostMapping("/images/{id}")
    public ResponseEntity <String> getUpdateImageUser(
            @PathVariable Integer id,
            @RequestPart("images") MultipartFile images){
        mypageService.getUpdateImage(id, images);
        return ResponseEntity.ok("유저프로필 수정 완료");
    }
    // 비밀번호 수정(=같은비밀번호 에러처리 완료, +UpdateAt)
    @PutMapping("/password/{id}")
    public ResponseEntity <String> getUpdatePasswordUser(
            @PathVariable Integer id,
            @Valid @RequestBody MypageUpdatePasswordRequest UpdatePasswordRequest){
        mypageService.getUpdatePasswordUser(id, UpdatePasswordRequest);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }
    // 찜 등록(= 찜을 한번 더 눌렀을경우 삭제 완료)
    @PostMapping("/wishlist/add/{campId}/{userId}")
    public ResponseEntity <String> registerWishlist(
            @PathVariable Integer campId, @PathVariable Integer userId){
        String message = mypageService.registerWishlist(campId, userId);
        return ResponseEntity.ok(message);
    }
    // 찜 조회
    @GetMapping("/wishlist/{userId}")
    public ResponseEntity <List<MypageCampResponse>> getWishList(
            @PathVariable Integer userId){
        List<MypageCampResponse> wishlist = mypageService.getWishList(userId);
        return ResponseEntity.ok(wishlist);
    }
    // 찜 삭제
    @DeleteMapping("/wishlist/remove/{wishId}")
    public ResponseEntity <String> removeWishList(
            @PathVariable Integer wishId){
        mypageService.removeWishList(wishId);
        return ResponseEntity.ok("찜 삭제 완료");
    }
}
