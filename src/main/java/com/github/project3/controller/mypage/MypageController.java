package com.github.project3.controller.mypage;

import com.github.project3.dto.mypage.*;
import com.github.project3.service.mypage.MypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

/**
 * 유저 마이페이지 관련 기능을 제공하는 컨트롤러입니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofile")
public class MypageController {

    private final MypageService mypageService;

    /**
     * 유저의 마이페이지 정보를 조회합니다.
     *
     * @param id 조회할 유저의 ID
     * @return 유저 마이페이지 정보 목록
     */
    @Operation(summary = "유저 마이페이지 정보 조회", description = "주어진 유저 ID에 대한 마이페이지 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<List<MypageResponse>> getUserMyPage(@PathVariable Integer id){
        List<MypageResponse> userMyPageResponse = mypageService.getUserMyPage(id);
        return ResponseEntity.ok(userMyPageResponse);
    }

    /**
     * 유저의 기본 정보를 수정합니다.
     *
     * @param id 수정할 유저의 ID
     * @param tel 수정할 전화번호 (선택 사항)
     * @param addr 수정할 주소 (선택 사항)
     * @return 수정 완료 메시지
     */
    @Operation(summary = "유저 기본정보 수정", description = "주어진 유저 ID의 전화번호와 주소를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<String> getUpdateUser(
            @PathVariable Integer id,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String addr){
        mypageService.getUpdateUser(id, tel, addr);
        return ResponseEntity.ok("유저정보 수정 완료");
    }

    /**
     * 유저의 프로필 이미지를 생성하거나 수정합니다.
     *
     * @param id 수정할 유저의 ID
     * @param images 업로드할 이미지 파일
     * @return 이미지 수정 완료 메시지
     */
    @Operation(summary = "유저 프로필 이미지 수정", description = "주어진 유저 ID의 프로필 이미지를 생성하거나 수정합니다.")
    @PostMapping("/images/{id}")
    public ResponseEntity<String> getUpdateImageUser(
            @PathVariable Integer id,
            @RequestPart("images") MultipartFile images){
        mypageService.getUpdateImage(id, images);
        return ResponseEntity.ok("유저프로필 수정 완료");
    }

    /**
     * 유저의 비밀번호를 수정합니다.
     *
     * @param id 비밀번호를 수정할 유저의 ID
     * @param UpdatePasswordRequest 비밀번호 수정 요청 정보
     * @return 비밀번호 변경 완료 메시지
     */
    @Operation(summary = "유저 비밀번호 수정", description = "주어진 유저 ID의 비밀번호를 수정합니다.")
    @PutMapping("/password/{id}")
    public ResponseEntity<String> getUpdatePasswordUser(
            @PathVariable Integer id,
            @Valid @RequestBody MypageUpdatePasswordRequest UpdatePasswordRequest){
        mypageService.getUpdatePasswordUser(id, UpdatePasswordRequest);
        return ResponseEntity.ok("비밀번호 변경 완료");
    }

    /**
     * 유저의 찜 목록에 캠핑을 등록하거나 등록된 캠핑을 삭제합니다.
     *
     * @param campId 등록할 캠핑의 ID
     * @param userId 찜을 등록할 유저의 ID
     * @return 찜 등록 또는 삭제 완료 메시지
     */
    @Operation(summary = "찜 등록", description = "주어진 캠핑 ID와 유저 ID로 찜을 등록하거나 등록된 찜을 삭제합니다.")
    @PostMapping("/wishlist/add/{campId}/{userId}")
    public ResponseEntity<String> registerWishlist(
            @PathVariable Integer campId, @PathVariable Integer userId){
        String message = mypageService.registerWishlist(campId, userId);
        return ResponseEntity.ok(message);
    }

    /**
     * 유저의 찜 목록을 조회합니다.
     *
     * @param userId 찜 목록을 조회할 유저의 ID
     * @return 유저의 찜 목록
     */
    @Operation(summary = "찜 목록 조회", description = "주어진 유저 ID에 대한 찜 목록을 조회합니다.")
    @GetMapping("/wishlist/{userId}")
    public ResponseEntity<List<MypageCampResponse>> getWishList(
            @PathVariable Integer userId){
        List<MypageCampResponse> wishlist = mypageService.getWishList(userId);
        return ResponseEntity.ok(wishlist);
    }

    /**
     * 유저의 찜 목록에서 찜을 삭제합니다.
     *
     * @param wishId 삭제할 찜의 ID
     * @return 찜 삭제 완료 메시지
     */
    @Operation(summary = "찜 삭제", description = "주어진 찜 ID로 찜 목록에서 찜을 삭제합니다.")
    @DeleteMapping("/wishlist/remove/{wishId}")
    public ResponseEntity<String> removeWishList(
            @PathVariable Integer wishId){
        mypageService.removeWishList(wishId);
        return ResponseEntity.ok("찜 삭제 완료");
    }

    // cash 사용내역 조회
    @GetMapping("/cashTransaction/{userId}")
    public ResponseEntity<List<CashTransactionResponse>> getUserCashTransactions(@PathVariable Integer userId){
        List<CashTransactionResponse> response = mypageService.getUserCashTransactions(userId);
        return ResponseEntity.ok(response);
    }
}
