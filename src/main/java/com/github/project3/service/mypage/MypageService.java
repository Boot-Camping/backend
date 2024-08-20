package com.github.project3.service.mypage;

import com.github.project3.dto.camp.CampResponse;
import com.github.project3.dto.mypage.*;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.entity.wishlist.WishlistEntity;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.mypage.MypageImageRepository;
import com.github.project3.repository.mypage.MypageRepository;
import com.github.project3.repository.mypage.NoticeRepository;
import com.github.project3.repository.mypage.WishlistRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.InvalidValueException;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
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

    // 유저 정보조회
    public List<MypageResponse> getUserMyPage(Integer id){
        Optional<UserEntity> user = mypageRepository.findById(id);
        return user.stream().map(this::convertToUserProfileResponse).collect(Collectors.toList());
    }
    private MypageResponse convertToUserProfileResponse(UserEntity userEntity){
        return new MypageResponse(
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
    public MypageUpdateResponse getUpdateUser(Integer id, String tel, String addr){
        UserEntity user = mypageRepository.findById(id).orElseThrow(() -> new NotFoundException("등록된 사용자를 찾을 수 없습니다."));

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
    public MypageUpdateImageResponse getUpdateImage(Integer id, MultipartFile images){
        UserEntity user = mypageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("등록된 사용자를 찾을 수 없습니다."));

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
    public MypageUpdatePasswordResponse getUpdatePasswordUser(Integer id, MypageUpdatePasswordRequest PasswordRequest) {
        UserEntity user = mypageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("등록된 사용자를 찾을 수 없습니다."));

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
        mypageRepository.save(user);

        return MypageUpdatePasswordResponse.from(user);
    }
    // 공지사항 조회
    public List<NoticeResponse> getNoticeAll(){
        List<NoticeEntity> notice = noticeRepository.findAllByOrderByCreatedAtDesc();

        if (notice.isEmpty()){
            throw new NotFoundException("등록된 공지사항이 없습니다.");
        }

        return notice.stream().map(NoticeResponse::from).collect(Collectors.toList());
    }
    // 공지사항 상세조회
    public ResponseEntity

    // 찜 등록
    public void registerWishlist(Integer campId, Integer userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("등록된 사용자를 찾을 수 없습니다."));

        CampEntity camp = campRepository.findById(campId).orElseThrow(() -> new NotFoundException("등록된 캠프를 찾을 수 없습니다."));

        boolean alreadyWishlist = wishlistRepository.existsByCampAndUser(camp, user);
        if (alreadyWishlist) {
            throw new NotAcceptException("이미 찜등록이 되어있는 상품입니다.");
        }

        WishlistEntity wishlist = new WishlistEntity();
        wishlist.setCamp(camp);
        wishlist.setUser(user);

        wishlistRepository.save(wishlist);
    }
    // 찜 조회
    public List<MypageCampResponse> getWishList(Integer userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("등록된 사용자를 찾을 수 없습니다."));

        List<WishlistEntity> wishlist = wishlistRepository.findByUser(user);

        if (wishlist.isEmpty()){
            throw new NotFoundException("등록된 찜 목록이 존재하지 않습니다.");
        }

        return wishlist.stream()
                .map(wishlistEntity -> MypageCampResponse.from(wishlistEntity.getCamp()))
                .collect(Collectors.toList());
    }
    // 찜 삭제
    public void removeWishList(Integer wishId){
        WishlistEntity wishlist = wishlistRepository.findById(wishId)
                .orElseThrow(() -> new NotFoundException("등록된 찜 내역이 존재하지 않습니다."));

        wishlistRepository.delete(wishlist);
    }
}
