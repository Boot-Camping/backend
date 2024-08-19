package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageUpdateResponse {
    private Integer id;
    private String tel;
    private String addr;

    // 스태틱 팩토리 메서드
    public static MypageUpdateResponse from(UserEntity user) {
        MypageUpdateResponse response = new MypageUpdateResponse();
        response.id = user.getId();
        response.tel = user.getTel();
        response.addr = user.getAddr();

        return response;
    }
}