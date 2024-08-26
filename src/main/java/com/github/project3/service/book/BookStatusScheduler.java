package com.github.project3.service.book;

import com.github.project3.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookStatusScheduler {

    private final BookRepository bookRepository;

    // 매일 자정(0초 0분 0시 매일 매월 모든요일)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateBookingStatus() {

        // 설정한 시간에 자동적으로 예약 상태 변경
        bookRepository.updateStatusIfEndDatePassed();
        System.out.println("예약 상태가 구매 확정으로 변경된 시간 : " + java.time.LocalDateTime.now());
    }
}