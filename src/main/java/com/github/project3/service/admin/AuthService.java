package com.github.project3.service.admin;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public void verifyAdmin(Integer userId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다"));

        if (user.getRole() != Role.ADMIN){
            throw new NotAcceptException("권한이 없습니다.");
        }
    }

}
