package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MypageUpdatePasswordResponse {
    private Integer id;
    private boolean newPassword;

    // 스태틱 팩토리 메서드
    public static MypageUpdatePasswordResponse from(UserEntity user) {
        return new MypageUpdatePasswordResponse(user.getId(), true);
    }
}
