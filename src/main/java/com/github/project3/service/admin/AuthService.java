package com.github.project3.service.admin;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.jwt.JwtTokenProvider;
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
    // 기간별 데이터(1일, 1주일, 1달)
    public <T extends CreatedAtRepository> long countEntityCreatedBetween(T repository, LocalDateTime start, LocalDateTime end) {
        return repository.countByCreatedAtBetween(start, end);
    }
    public <B extends BookRepository> long sumTotalPriceByCreatedAtBetween(B repository, LocalDateTime start, LocalDateTime end){
        return repository.sumTotalPriceByCreatedAtBetween(start, end);
    }

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
    public long getLastDayBalance(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return sumTotalPriceByCreatedAtBetween(bookRepository, oneDayAgo, now);
    }
    public long getLastWeekBalance(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return sumTotalPriceByCreatedAtBetween(bookRepository, oneWeekAgo, now);
    }
    public long getLastMonthBalance(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return sumTotalPriceByCreatedAtBetween(bookRepository, oneMonthAgo, now);
    }

    public long getTotalUserCount(UserRepository userRepository){
        return userRepository.count();
    }
    public long getTotalBookCount(BookRepository bookRepository){
        return bookRepository.count();
    }
    public long getTotalBalance(BookRepository bookRepository){
        return bookRepository.sumTotalPrice();
    }

}
