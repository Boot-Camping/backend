package com.github.project3.entity.user;

import com.github.project3.entity.user.enums.Role;
import com.github.project3.entity.user.enums.Status;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class CustomUserDetails implements UserDetails {


    private Integer id;
    private String loginId;
    private String password;
    private Status status;
    private Role role;

    // 권한 목록
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Integer id, String loginId, String password, Status status, Role role, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.status = status;
        this.role = role;
        this.authorities = authorities;
    }

    // 사용자의 권한 반환 (role에 따라 다르게 권한을 부여할 수 있음)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    // 계정 만료 여부 (여기선 항상 true로 설정, 필요에 따라 다르게 설정 가능)
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않음
    }

    // 계정 잠김 여부 (BLACKLIST 상태일 경우 계정을 잠금 처리)
    @Override
    public boolean isAccountNonLocked() {
        return this.status != Status.BLACKLIST; // BLACKLIST 상태가 아니면 계정이 잠기지 않음
    }

    // 자격 증명(비밀번호) 만료 여부 (여기선 항상 true로 설정)
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명이 만료되지 않음
    }

    // 계정 활성화 여부 (DELETE 상태일 경우 비활성화 처리)
    @Override
    public boolean isEnabled() {
        return this.status == Status.ACTIVE; // 계정이 ACTIVE 상태일 때만 활성화됨
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", status=" + status +
                ", role=" + role +
                ", authorities=" + authorities +
                '}';
    }
}

