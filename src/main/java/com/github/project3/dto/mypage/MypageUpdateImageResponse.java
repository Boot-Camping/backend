package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserImageEntity;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageUpdateImageResponse {
    private Integer id;
    private String profileImageUrl;

    // 스태틱 팩토리 메서드
    public static MypageUpdateImageResponse from(UserImageEntity userImage) {
        MypageUpdateImageResponse response = new MypageUpdateImageResponse();
        response.id = userImage.getId();
        response.profileImageUrl = userImage.getImageUrl();

        return response;
    }


}