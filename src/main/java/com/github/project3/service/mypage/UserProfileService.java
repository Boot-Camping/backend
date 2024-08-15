package com.github.project3.service.mypage;

import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.repository.mypage.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

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
                userEntity.getCash().stream().max(Comparator.comparing(CashEntity::getTransactionDate)).map(CashEntity::getBalanceAfterTransaction).orElse(0)
        );
    }
}
