package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserImageEntity;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateImageResponse {
    private Integer id;
    private String profileImageUrl;

    // 스태틱 팩토리 메서드
    public static UserProfileUpdateImageResponse from(UserImageEntity userImage) {
        UserProfileUpdateImageResponse response = new UserProfileUpdateImageResponse();
        response.id = userImage.getId();
        response.profileImageUrl = userImage.getImageUrl();

        return response;
    }


}