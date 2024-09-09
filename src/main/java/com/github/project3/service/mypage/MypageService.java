package com.github.project3.service.mypage;

import com.github.project3.dto.mypage.*;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.entity.wishlist.WishlistEntity;
import com.github.project3.entity.wishlist.enums.Status;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.repository.mypage.MypageImageRepository;
import com.github.project3.repository.mypage.MypageRepository;
import com.github.project3.repository.mypage.NoticeRepository;
import com.github.project3.repository.mypage.WishlistRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.InvalidValueException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.user.UserService;
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
public class MypageService {

    private final MypageImageRepository mypageImageRepository;
    private final MypageRepository mypageRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final CampRepository campRepository;
    private final WishlistRepository wishlistRepository;
    private final S3Service s3Service;
    private final CashRepository cashRepository;
    private final UserService userService;

    // 유저 정보조회
    public List<MypageResponse> getUserMyPage(Integer userId){
        Optional<UserEntity> user = userRepository.findById(userId);
        return user.stream().map(this::convertToUserProfileResponse).collect(Collectors.toList());
    }
    private MypageResponse convertToUserProfileResponse(UserEntity userEntity){
        return new MypageResponse(
                userEntity.getId(),
                userEntity.getLoginId(),
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
    public MypageUpdateResponse getUpdateUser(String tel, String addr){
        UserEntity user = userService.findAuthenticatedUser();

        if (tel != null){
            user.setTel(tel);
        }
        if (addr != null){
            user.setAddr(addr);
        }

        mypageRepository.save(user);

        return MypageUpdateResponse.from(user);
    }

    // 유저 이미지 수정
    @Transactional
    public MypageUpdateImageResponse getUpdateImage(MultipartFile images){
        UserEntity user = userService.findAuthenticatedUser();

        UserImageEntity userImage = mypageImageRepository.findByUserId(user)
                // 값을 갖고있지 않은경우
                .orElseGet(() -> {
                    UserImageEntity newUserImage = new UserImageEntity();
                    newUserImage.setUserId(user);
                    return newUserImage;
                });

        try {
            // 프로필 이미지 업로드
            String profileImageUrl = s3Service.uploadUserImage(images);

            // 프로필 이미지 URL 업데이트
            userImage.setImageUrl(profileImageUrl);
            mypageImageRepository.save(userImage);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("알수없는 오류가 발생했습니다.");
        }
        return MypageUpdateImageResponse.from(userImage);
    }

    // 유저 비밀번호 수정
    @Transactional
    public MypageUpdatePasswordResponse getUpdatePasswordUser(MypageUpdatePasswordRequest PasswordRequest) {
        UserEntity user = userService.findAuthenticatedUser();

        // 비밀번호 패턴 검증 추가
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&*!+=]{8,20}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(PasswordRequest.getNewPassword());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("비밀번호는 영문자(필수), 숫자(필수), 특수문자의 조합으로 8자 이상 20자 이하로 설정해주세요");
        }

        if (passwordEncoder.matches(PasswordRequest.getNewPassword(), user.getPassword())){
            throw new IllegalArgumentException("새 비밀번호가 기존 비밀번호와 동일합니다.");
        }

        if (!passwordEncoder.matches(PasswordRequest.getOldPassword(), user.getPassword())){
            throw new InvalidValueException("검증 비밀번호가 기존 비밀번호와 동일하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(PasswordRequest.getNewPassword()));
        mypageRepository.save(user);

        return MypageUpdatePasswordResponse.from(user);
    }

    // 찜 등록
    @Transactional
    public String registerWishlist(Integer campId){
        UserEntity user = userService.findAuthenticatedUser();

        CampEntity camp = campRepository.findById(campId).orElseThrow(() -> new NotFoundException("등록된 캠프를 찾을 수 없습니다."));

        WishlistEntity alreadyWishlist = wishlistRepository.findByCampAndUser(camp, user);
        if (alreadyWishlist != null) {
            if (alreadyWishlist.getStatus() == Status.DELETE){
                alreadyWishlist.setStatus(Status.ACTIVE);
                wishlistRepository.save(alreadyWishlist);
                return "찜 등록 완료";
            } else {
                alreadyWishlist.setStatus(Status.DELETE);
                wishlistRepository.save(alreadyWishlist);
                return "찜 삭제 완료";
            }
        } else {
        WishlistEntity wishlist = new WishlistEntity();
        wishlist.setCamp(camp);
        wishlist.setUser(user);
        wishlist.setStatus(Status.ACTIVE);

        wishlistRepository.save(wishlist);

        return "찜 등록 완료";
        }
    }
    // 찜 조회
    @Transactional(readOnly = true)
    public List<MypageCampResponse> getWishList(){
        UserEntity user = userService.findAuthenticatedUser();

        List<WishlistEntity> wishlistUser = wishlistRepository.findByUserAndStatus(user, Status.ACTIVE);

        if (wishlistUser.isEmpty()){
            throw new NotFoundException("등록된 찜 목록이 존재하지 않습니다.");
        }
            return wishlistUser.stream()
                    .map(MypageCampResponse::from).collect(Collectors.toList());

    }
    // 찜 삭제
    @Transactional
    public void removeWishList(Integer wishId){
        UserEntity user = userService.findAuthenticatedUser();
        WishlistEntity wishlist = wishlistRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("등록된 찜 내역이 존재하지 않습니다."));

        if (!wishlist.getUser().equals(user)) {
            throw new NotFoundException("삭제 권한이 없는 사용자 입니다.");
        } else if (wishlist.getStatus() == Status.DELETE){
            throw new NotFoundException("이미 삭제된 찜입니다.");
        } else {
                wishlist.setUser(user);
                wishlist.setStatus(Status.DELETE);
                wishlistRepository.save(wishlist);
            }
    }

    // cash 사용내역 조회
    public List<CashTransactionResponse> getUserCashTransactions(Integer userId) {
        return cashRepository.findCashTransactionsWithCampNameByUserId(userId);
    }
}
