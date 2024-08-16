package com.github.project3.dto.mypage;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdatePasswordRequest {
    private String oldPassword;
    private String newPassword;

    // 스태틱 팩토리 메서드
    public static UserProfileUpdatePasswordRequest from(String oldPassword, String newPassword) {
        UserProfileUpdatePasswordRequest request = new UserProfileUpdatePasswordRequest();
        // 문자열 검사후 패턴과 일치하는지 확인
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher match = pattern.matcher(newPassword);

        if (!match.matches()){
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자의 조합으로 8자 이상 20자 이하로 설정해주세요");
        }

        request.setOldPassword(oldPassword);
        request.setNewPassword(newPassword);

        return request;
    }

}
