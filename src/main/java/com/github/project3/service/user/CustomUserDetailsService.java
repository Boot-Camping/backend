package com.github.project3.service.user;

import com.github.project3.entity.user.CustomUserDetails;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;



@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        // UserRepository를 통해 로그인 ID로 사용자 조회
        UserEntity user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + loginId));

        // 조회된 사용자 정보를 CustomUserDetails로 변환
        return new CustomUserDetails(
                user.getId(),
                user.getLoginId(),
                user.getPassword(),
                user.getStatus(),
                user.getRole(),
                getAuthorities(user)
        );
    }

    // 사용자의 권한(roles)을 GrantedAuthority로 변환하는 메서드
    private Collection<? extends GrantedAuthority> getAuthorities(UserEntity user) {
        // 예시: Role에 따른 권한 부여
        if (user.getRole() == Role.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ADMIN"));
        } else {
            return List.of(new SimpleGrantedAuthority("`USER"));
        }
    }

}

