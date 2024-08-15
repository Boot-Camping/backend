package com.github.project3.controller.mypage;

import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.service.mypage.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userprofile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<List<UserProfileResponse>> getUserMyPage(@PathVariable Integer id){
        List<UserProfileResponse> userMyPageResponse = userProfileService.getUserMyPage(id);
        return ResponseEntity.ok(userMyPageResponse);
    }
}
