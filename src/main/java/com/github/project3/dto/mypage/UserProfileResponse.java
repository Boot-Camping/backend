package com.github.project3.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer id;
    private String name;
    private String password;
    private String email;
    private String tel;
    private List<String> images;
    private Integer balance;
}
