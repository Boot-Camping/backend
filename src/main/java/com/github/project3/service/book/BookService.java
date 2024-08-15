package com.github.project3.service.book;

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

    @Transactional
    public void registerBook(Integer campId, Integer userId, BookRegisterRequest bookRegisterRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        CampEntity camp = campRepository.findById(campId).orElseThrow(()-> new NotFoundException("해당하는 캠핑지가 존재하지 않습니다."));

        // 찾은 user 의 현재 잔액
        Integer cashValue = user.getCash().stream()
                .max(Comparator.comparing(CashEntity::getTransactionDate))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);
        log.info("User 남은 잔액 : " + String.valueOf(cashValue));

        // user 의 cash 변경
        Integer totalPrice = bookRegisterRequest.getTotalPrice();
        Integer newBalance = cashValue - totalPrice;

        if(totalPrice > cashValue){
            throw new NotAcceptException("고객님의 현재 잔액은 " + cashValue + "원 입니다. 예약이 취소됩니다.");
        }

        CashEntity cash = CashEntity.of(
                user,
                totalPrice,
                TransactionType.PAYMENT,
                newBalance
        );
        cashRepository.save(cash);

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

        while (!requestCheckIn.isAfter(requestCheckOut)) {
            BookDateEntity bookDate = new BookDateEntity();
            bookDate.setBook(savedBook);  // savedBook ID 사용
            bookDate.setDate(requestCheckIn);
            bookDates.add(bookDate);

            requestCheckIn = requestCheckIn.plusDays(1);
        }
        bookDateRepository.saveAll(bookDates);
    }
}
