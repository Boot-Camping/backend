package com.github.project3.service.mypage;

import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.dto.mypage.UserProfileUpdateImageResponse;
import com.github.project3.dto.mypage.UserProfileUpdatePasswordRequest;
import com.github.project3.dto.mypage.UserProfileUpdateResponse;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.repository.mypage.UserProfileImageRepository;
import com.github.project3.repository.mypage.UserProfileRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
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

    public void getUpdatePasswordUser(Integer id, UserProfileUpdatePasswordRequest UpdatePasswordRequest) {
//        UserEntity user = UserProfileRepository.findById(id)
//                .orElseThrow(() -> )
        //
    }
}
