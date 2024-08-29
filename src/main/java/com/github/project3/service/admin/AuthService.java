package com.github.project3.service.admin;

import com.github.project3.entity.admin.AdminEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.admin.AdminNoticeRepository;
import com.github.project3.repository.admin.AdminRepository;
import com.github.project3.repository.admin.CreatedAtRepository;
import com.github.project3.repository.book.BookRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;

    // 관리자 인증
    public void verifyAdmin(String token){
        Integer userId = jwtTokenProvider.getUserId(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다"));

        if (user.getRole() != Role.ADMIN){
            throw new NotAcceptException("권한이 없습니다.");
        }
    }
    // 블랙리스트 차단
    public void verifyNotBlacklisted(UserEntity user) {
        if (user.getStatus() == Status.BLACKLIST) {
            throw new NotAcceptException("블랙리스트 회원입니다. 로그인이 불가능합니다.");
        }
    }
    // 관리자 잔액조회
    public Integer getSales(UserEntity user) {
        adminRepository.deleteAll();

        return adminRepository.findByUser(user)
                .map(AdminEntity::getSales)
                .orElse(0); // 매출액이 없는 경우 0 반환
    }
    // CreateAt 기준 Count수 / StartDate기준 전체 매출액
    public <T extends CreatedAtRepository> long countEntityCreatedBetween(T repository, LocalDateTime start, LocalDateTime end) {
        return repository.countByCreatedAtBetween(start, end);
    }
    public <B extends BookRepository> Long sumTotalPriceByStartDateBetween(B repository, LocalDateTime start, LocalDateTime end){
        return repository.sumTotalPriceByStartDateBetween(start, end);
    }
    // + 기간별 데이터(1일, 1주일, 1달, 전체)
    public long getLastDayCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return countEntityCreatedBetween(createdAtRepository, oneDayAgo, now);
    }
    public long getLastWeekCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return countEntityCreatedBetween(createdAtRepository, oneWeekAgo, now);
    }
    public long getLastMonthCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return countEntityCreatedBetween(createdAtRepository, oneMonthAgo, now);
    }
    public long getLastDaySales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneDayAgo, now);
    }
    public long getLastWeekSales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneWeekAgo, now);
    }
    public long getLastMonthSales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneMonthAgo, now);
    }

    public long getTotalUserCount(UserRepository userRepository){
        return userRepository.count();
    }
    public long getTotalBookCount(BookRepository bookRepository){
        return bookRepository.count();
    }
    public long getTotalSales(BookRepository bookRepository){
        return bookRepository.sumTotalPrice();
    }

}
