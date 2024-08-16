package com.github.project3.service.book;

import com.github.project3.dto.book.BookCancelResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.BookDateEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.repository.book.BookRepository;
import com.github.project3.repository.bookDate.BookDateRepository;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookDateRepository bookDateRepository;
    private final CashRepository cashRepository;
    private final CampRepository campRepository;

    // 예약 등록 기능
    @Transactional
    public void registerBook(Integer campId, Integer userId, BookRegisterRequest bookRegisterRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        CampEntity camp = campRepository.findById(campId).orElseThrow(()-> new NotFoundException("해당하는 캠핑지가 존재하지 않습니다."));

        // 예약 상태 확인
        boolean isBookingExists = bookRepository.existsByUserAndCampAndStatus(user, camp, Status.BOOKING);

        if (isBookingExists) {
            throw new NotAcceptException("해당 캠핑지는 이미 예약된 상태입니다. 예약이 취소됩니다.");
        }

        // 찾은 user 의 현재 잔액
        Integer cashValue = user.getCash().stream()
                .max(Comparator.comparing(CashEntity::getTransactionDate))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);

        // user 의 cash 변경
        Integer totalPrice = bookRegisterRequest.getTotalPrice();
        Integer newBalance = cashValue - totalPrice;

        if(totalPrice > cashValue){
            throw new NotAcceptException("고객님의 현재 잔액은 " + cashValue + "원 입니다. 예약이 취소됩니다.");
        }

        CashEntity paymentCash = CashEntity.of(
                user,
                totalPrice,
                TransactionType.PAYMENT,
                newBalance
        );
        cashRepository.save(paymentCash);

        // 예약 정보 등록
        BookEntity book = BookEntity.of(
                user,
                camp,
                bookRegisterRequest.getTotalPrice(),
                bookRegisterRequest.getCheckIn(),
                bookRegisterRequest.getCheckOut(),
                bookRegisterRequest.getBookRequest(),
                bookRegisterRequest.getBookNum(),
                Status.BOOKING
        );

        BookEntity savedBook = bookRepository.save(book);

        // 예약 날짜 등록
        List<BookDateEntity> bookDates = new ArrayList<>();
        LocalDateTime requestCheckIn = bookRegisterRequest.getCheckIn();
        LocalDateTime requestCheckOut = bookRegisterRequest.getCheckOut();

        // 반복문으로 체크인날짜, 체크아웃날짜, 중간에 있는 날짜들도 DB에 저장
        while (!requestCheckIn.isAfter(requestCheckOut)) {
            BookDateEntity bookDate = new BookDateEntity();
            bookDate.setBook(savedBook);  // savedBook ID 사용
            bookDate.setDate(requestCheckIn);
            bookDates.add(bookDate);

            requestCheckIn = requestCheckIn.plusDays(1);
        }
        bookDateRepository.saveAll(bookDates);
    }

    // 예약 취소 기능
    @Transactional
    public BookCancelResponse cancelBook(Integer bookId, Integer userId) {

        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("해당 ID의 예약이 존재하지 않습니다."));

        if (book.getStatus() == Status.CANCEL) {
            throw new NotAcceptException("해당 예약은 이미 취소된 상태입니다.");
        }

        // 예약 상태를 취소로 변경
        book.setStatus(Status.CANCEL);
        bookRepository.save(book);

        // 찾은 user 의 현재 잔액
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        Integer cashValue = user.getCash().stream()
                .max(Comparator.comparing(CashEntity::getTransactionDate))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);

        // user 의 cash 환불
        Integer totalPrice = book.getTotalPrice();
        Integer newBalance = cashValue + totalPrice;

        CashEntity refundCash = CashEntity.of(
                book.getUser(),
                totalPrice,
                TransactionType.REFUND,
                newBalance
        );
        cashRepository.save(refundCash);

        // 환불하고 남은 잔액 반환
        return BookCancelResponse.builder()
                .totalPrice(newBalance)
                .build();
    }
}
