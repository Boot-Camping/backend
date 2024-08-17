package com.github.project3.service.mypage;

import com.github.project3.dto.mypage.*;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.repository.mypage.UserProfileImageRepository;
import com.github.project3.repository.mypage.UserProfileRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.InvalidValueException;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileImageRepository userProfileImageRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    // 유저 정보조회
    public List<UserProfileResponse> getUserMyPage(Integer id){
        Optional<UserEntity> user = userProfileRepository.findById(id);
        return user.stream().map(this::convertToUserProfileResponse).collect(Collectors.toList());
    }
    private UserProfileResponse convertToUserProfileResponse(UserEntity userEntity){
        return new UserProfileResponse(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getPassword(),
                userEntity.getEmail(),
                userEntity.getTel(),
                userEntity.getImages().stream().map(UserImageEntity::getImageUrl).collect(Collectors.toList()),
                userEntity.getCash().stream().max(Comparator.comparing(CashEntity::getTransactionDate)).map(CashEntity::getBalanceAfterTransaction).orElse(0),
                userEntity.getAddr()
        );
    }

    // 유저 정보 수정
    @Transactional
    public UserProfileUpdateResponse getUpdateUser(Integer id, String tel, String addr){
        UserEntity user = userProfileRepository.findById(id).orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        if (tel != null){
            user.setTel(tel);
        }
        if (addr != null){
            user.setAddr(addr);
        }

        userProfileRepository.save(user);

        return UserProfileUpdateResponse.from(user);
    }

    // 유저 이미지 수정
    @Transactional
    public UserProfileUpdateImageResponse getUpdateImage(Integer id, MultipartFile images){
        UserEntity user = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        UserImageEntity userImage = userProfileImageRepository.findByUserId(user)
                // 값을 갖고있지 않은경우
                .orElseGet(() -> {
                    UserImageEntity newUserImage = new UserImageEntity();
                    newUserImage.setUserId(user);
                    return newUserImage;
                });

        try {
            // 프로필 이미지 업로드
            String profileImageUrl = s3Service.uploadFile(images);

            // 프로필 이미지 URL 업데이트
            userImage.setImageUrl(profileImageUrl);
            userProfileImageRepository.save(userImage);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("알수없는 오류가 발생했습니다.");
        }
        return UserProfileUpdateImageResponse.from(userImage);
    }

    // 유저 비밀번호 수정
    public UserProfileUpdatePasswordResponse getUpdatePasswordUser(Integer id, UserProfileUpdatePasswordRequest PasswordRequest) {
        UserEntity user = userProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 비밀번호 패턴 검증 추가
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(PasswordRequest.getNewPassword());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("비밀번호는 영문자, 숫자의 조합으로 8자 이상 20자 이하로 설정해주세요");
        }


        if (passwordEncoder.matches(PasswordRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(PasswordRequest.getNewPassword()));
        } else {
            throw new InvalidValueException("비밀번호가 다릅니다.");
        }
        userProfileRepository.save(user);

        return UserProfileUpdatePasswordResponse.from(user);
    }
}
