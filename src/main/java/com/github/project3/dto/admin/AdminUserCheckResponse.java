package com.github.project3.dto.admin;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserCheckResponse {
    private Integer Id;
    private String loginId;
    private String name;
    private String email;
    private String tel;
    private Status status;

    public static AdminUserCheckResponse from(UserEntity user){
        AdminUserCheckResponse response = new AdminUserCheckResponse();
        response.Id = user.getId();
        response.loginId = user.getLoginId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.tel = user.getTel();
        response.status = user.getStatus();

        return response;
    }
    public static List<AdminUserCheckResponse> from(List<UserEntity> user){
        return user.stream().map(AdminUserCheckResponse::from).collect(Collectors.toList());
    }
}
