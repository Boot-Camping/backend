package com.github.project3.dto.user.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {

    @NotBlank(message = "이메일을 입력하세요")
    @Email(message = "이메일을 올바르게 입력하세요")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "비밀번호는 영문자, 숫자의 조합으로 8자 이상 20자 이하로 설정해주세요")
    private String password;

    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "ID는 특수문자를 제외한 2~10자리여야 합니다.")
    private String loginId;

    @NotBlank(message = "이름을 입력하세요")
    private String name;

    @NotBlank(message = "전화번호를 입력하세요")
    private String tel;

    @NotBlank(message = "주소를 입력하세요")
    private String addr;
}
