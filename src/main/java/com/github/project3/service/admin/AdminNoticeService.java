package com.github.project3.service.admin;

import com.github.project3.dto.admin.*;
import com.github.project3.dto.admin.AdminNoticeRegisterResponse;
import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.admin.AdminNoticeImageRepository;
import com.github.project3.repository.admin.AdminNoticeRepository;
import com.github.project3.repository.book.BookRepository;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final AdminNoticeRepository adminNoticeRepository;
    private final AdminNoticeImageRepository adminNoticeImageRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final AuthService authService;
    private final BookRepository bookRepository;
    private final CashService cashService;
    private final CashRepository cashRepository;

    // 공지사항 등록
    @Transactional
    public AdminNoticeRegisterResponse registerNotice(AdminNoticeRegisterRequest registerRequest, List<MultipartFile> images, String token){
        authService.verifyAdmin(token);

        NoticeEntity notice = new NoticeEntity();
        if (registerRequest.getTitle() == null || registerRequest.getTitle().isEmpty()){
            throw new IllegalArgumentException("공지 제목을 입력해주세요.");
        } else if (registerRequest.getDescription() == null || registerRequest.getDescription().isEmpty()) {
            throw new IllegalArgumentException("공지 내용을 입력해주세요.");
        }
        notice.setTitle(registerRequest.getTitle());
        notice.setDescription(registerRequest.getDescription());

        adminNoticeRepository.save(notice);

        if (!images.isEmpty() && images != null){
            for (MultipartFile image : images){
                try {
                    String imageUrl = s3Service.uploadNoticeImage(image);

                    NoticeImageEntity noticeImage = new NoticeImageEntity();
                    noticeImage.setNotice(notice); // 공지사항과 연결
                    noticeImage.setImageUrl(imageUrl);

                    adminNoticeImageRepository.save(noticeImage);
                } catch (IOException e){
                    throw new RuntimeException("이미지 업로드 실패", e);
                }
            }
        }
        return AdminNoticeRegisterResponse.from(notice);
    }
    // 공지사항 전체조회
    public Page<AdminNoticeCheckResponse> getNoticeAll(Integer page,Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeEntity> noticePage = adminNoticeRepository.findAllByOrderByCreatedAtDesc(pageable);
        if (noticePage == null && noticePage.isEmpty()){
            throw new NotFoundException("등록된 공지사항이 없습니다.");
        }

        return noticePage.map(AdminNoticeCheckResponse::from);
    }
    // 공지사항 상세조회
    public AdminNoticeDetailCheckResponse getNoticeDetail(Integer id){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 공지사항을 찾을 수 없습니다."));

        return AdminNoticeDetailCheckResponse.from(notice);
    }

    // 공지사항 수정
    @Transactional
    public AdminNoticeUpdateResponse getUpdateNotice(Integer id, AdminNoticeUpdateRequest noticeUpdateRequest, List<MultipartFile> images, String token){
        authService.verifyAdmin(token);

        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 공지사항을 찾을 수 없습니다."));
        if (noticeUpdateRequest.getTitle() == null && noticeUpdateRequest.getDescription() == null){
            throw new NotFoundException("수정사항을 입력해 주세요.");
        }
        if (noticeUpdateRequest.getTitle() == null){
            notice.setDescription(noticeUpdateRequest.getDescription());
        } else if (noticeUpdateRequest.getDescription() == null) {
            notice.setTitle(noticeUpdateRequest.getTitle());
        } else {
            notice.setDescription(noticeUpdateRequest.getDescription());
            notice.setTitle(noticeUpdateRequest.getTitle());
        }
            // 기존 이미지 삭제
            List<NoticeImageEntity> removeImages = notice.getImages();
            if (removeImages != null && !removeImages.isEmpty()) {
                adminNoticeImageRepository.deleteAll(removeImages);
                notice.setImages(new ArrayList<>());

            // 이미지 추가
            if (images != null || !images.isEmpty()){
                for (MultipartFile image: images){
                    try {
                        String imageUrl = s3Service.uploadNoticeImage(image);
                        NoticeImageEntity noticeImage = new NoticeImageEntity();
                        noticeImage.setNotice(notice);
                        noticeImage.setImageUrl(imageUrl);

                        adminNoticeImageRepository.save(noticeImage);
                        notice.getImages().add(noticeImage);
                    } catch (IOException e){
                        throw new RuntimeException("이미지 업로드 실패", e);
                    }
                }
            }
            adminNoticeRepository.save(notice);
        }
        return AdminNoticeUpdateResponse.from(notice);
    }

    // 공지사항 삭제
    @Transactional
    public void removeNotice(Integer id, String token){
        authService.verifyAdmin(token);

        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 입니다."));

        adminNoticeRepository.delete(notice);
    }

    // 사이트 통계 (매출추이, 유저추이, 예약수, (카테고리별 예약자수))
    public AdminDataResponse getAllData(String token){
        authService.verifyAdmin(token);
        // 유저수
        long lastDayUserCount = authService.getLastDayCount(userRepository);
        long lastWeekUserCount = authService.getLastWeekCount(userRepository);
        long lastMonthUserCount = authService.getLastMonthCount(userRepository);
        long totalUserCount = authService.getTotalUserCount(userRepository);
        // 예약수
        long lastDayBookCount = authService.getLastDayCount(bookRepository);
        long lastWeekBookCount = authService.getLastWeekCount(bookRepository);
        long lastMonthBookCount = authService.getLastMonthCount(bookRepository);
        long totalBookCount = authService.getTotalBookCount(bookRepository);
        // 매출액
        long lastDayAdminBalance = authService.getLastDayBalance(bookRepository);
        long lastWeekAdminBalance = authService.getLastWeekBalance(bookRepository);
        long lastMonthAdminBalance = authService.getLastMonthBalance(bookRepository);
        long totalAdminBalance = authService.getTotalBalance(bookRepository);

        return AdminDataResponse.from(
                lastDayUserCount, lastWeekUserCount, lastMonthUserCount, totalUserCount,
                lastDayBookCount, lastWeekBookCount, lastMonthBookCount, totalBookCount,
                lastDayAdminBalance, lastWeekAdminBalance, lastMonthAdminBalance, totalAdminBalance);

    }

    // 유저 조회
    public List<AdminUserCheckResponse> getUserAll(String token){
        authService.verifyAdmin(token);

        List<UserEntity> user = userRepository.findAllByOrderByCreatedAtDesc();
        if (user == null && user.isEmpty()){
            throw new NotFoundException("유저가 존재하지 않습니다.");
        }
        return AdminUserCheckResponse.from(user);
    }

    // 회원 블랙리스트 등록
    @Transactional
    public void getBlacklist(Integer id, String token){
        authService.verifyAdmin(token);

        UserEntity user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        if (user.getStatus() == Status.BLACKLIST){
            throw new NotAcceptException("이미 등록된 블랙리스트 입니다.");
        }
        user.setStatus(Status.BLACKLIST);
        userRepository.save(user);
    }

    // 관리자 통장 업데이트(스케줄러-1시간마다)
    @Transactional
    public void updateAdminBalance(){
        LocalDateTime now = LocalDateTime.now();
        // decide 조회
        List<BookEntity> decideBook = bookRepository.findAllByStartDateBeforeAndStatus(now, com.github.project3.entity.book.enums.Status.BOOKING);

        // StartDate가 지난 캠핑 DECIDE로 업데이트
        decideBook.forEach(book -> book.setStatus(com.github.project3.entity.book.enums.Status.DECIDE));
        bookRepository.saveAll(decideBook);

        if (!decideBook.isEmpty()){
            UserEntity adminUser = userRepository.findByRole(Role.ADMIN).orElseThrow(() -> new NotFoundException("관리자 유저가 존재하지 않습니다."));
            // decide 상태의 캠프 총합계
            int totalPrice = decideBook.stream().mapToInt(BookEntity::getTotalPrice).sum();

            Integer currentBalance = cashService.getCurrentBalance(adminUser);

            Integer newBalance = currentBalance + totalPrice;
            // cash 업데이트
            CashEntity cashTransaction = CashEntity.of(
                    adminUser,
                    totalPrice,
                    TransactionType.DEPOSIT,
                    newBalance
            );
            cashRepository.save(cashTransaction);
            // 관리자 cash 업데이트
            adminUser.getCash().add(cashTransaction);

            userRepository.save(adminUser);

        }
    }


}
