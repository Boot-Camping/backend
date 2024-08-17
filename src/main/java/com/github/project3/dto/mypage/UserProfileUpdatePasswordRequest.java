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

}
