package com.github.project3.service.mypage;

import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.dto.mypage.UserProfileUpdateImageResponse;
import com.github.project3.dto.mypage.UserProfileUpdateResponse;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.repository.mypage.UserProfileImageRepository;
import com.github.project3.repository.mypage.UserProfileRepository;
import com.github.project3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileImageRepository userProfileImageRepository;
    private final UserProfileRepository userProfileRepository;
    private final S3Service s3Service;

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

    @Transactional
    public UserProfileUpdateResponse getUpdateUser(Integer id, String tel, String addr){
        UserEntity user = userProfileRepository.findById(id).orElseThrow(() -> new RuntimeException("사용자를 찾을수 없습니다."));

        if (tel != null){
            user.setTel(tel);
        }
        if (addr != null){
            user.setAddr(addr);
        }

        userProfileRepository.save(user);

        // return new UserProfileUpdateResponse(user.getId(), user.getTel(), user.getAddr());
        return UserProfileUpdateResponse.from(user);
    }

    @Transactional
    public UserProfileUpdateImageResponse getUpdateImage(Integer id, MultipartFile images){
        UserImageEntity user = userProfileImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을수 없습니다."));

        try {
            // 프로필 이미지 업로드
            String profileImageUrl = s3Service.uploadFile(images);

            // 프로필 이미지 URL 업데이트
            user.setImageUrl(profileImageUrl);
            userProfileImageRepository.save(user);
        }catch (IOException e){
            throw new RuntimeException("이미지 업로드에 오류가 생겼습니다", e);
        }
        return UserProfileUpdateImageResponse.from(user);
    }
}
