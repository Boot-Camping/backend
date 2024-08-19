package com.github.project3.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MypageUpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;

}
