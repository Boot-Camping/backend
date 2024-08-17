package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdatePasswordResponse {
    private Integer id;
    private boolean newPassword;

    // 스태틱 팩토리 메서드
    public static UserProfileUpdatePasswordResponse from(UserEntity user) {
        return new UserProfileUpdatePasswordResponse(user.getId(), true);
    }
}
