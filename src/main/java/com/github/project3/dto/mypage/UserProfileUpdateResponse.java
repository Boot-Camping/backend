package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateResponse {
    private Integer id;
    private String tel;
    private String addr;

    // 스태틱 팩토리 메서드
    public static UserProfileUpdateResponse from(UserEntity user) {
        UserProfileUpdateResponse response = new UserProfileUpdateResponse();
        response.id = user.getId();
        response.tel = user.getTel();
        response.addr = user.getAddr();

        return response;
    }
}