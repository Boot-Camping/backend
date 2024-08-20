package com.github.project3.service.book;

import com.github.project3.dto.book.BookCancelResponse;
import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.dto.camp.CampResponse;
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
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final UserService userService;
    private final CashService cashService;
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

        LocalDateTime requestCheckIn = bookRegisterRequest.getCheckIn();
        LocalDateTime requestCheckOut = bookRegisterRequest.getCheckOut();

        List<Status> statuses = Arrays.asList(Status.BOOKING, Status.DECIDE);

        // 예약 날짜 중복 확인
        boolean isDateConflict = bookRepository.existsByCampAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusIn(
                camp,
                requestCheckIn,
                requestCheckOut,
                statuses
        );

        if (isDateConflict) {
            throw new NotAcceptException("해당 날짜에 이미 예약이 존재합니다. 다른 날짜를 선택해주세요.");
        }

        // 예약 날짜와 요청 시간의 차이 계산
        LocalDateTime now = LocalDateTime.now();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(now, requestCheckIn);

        // 예약 날짜에 임박하면(2일 이내) 예약금 10,000원 할인
        int totalPrice = bookRegisterRequest.getTotalPrice();
        if (daysUntilCheckIn <= 2) {
            totalPrice -= 10000;
        }

        // user 의 cash 변동사항 저장
        cashService.processTransaction(user, totalPrice, TransactionType.PAYMENT);

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
    public Integer cancelBook(Integer bookId, Integer userId) {

        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("해당하는 예약이 존재하지 않습니다."));

        if (book.getStatus() == Status.CANCEL) {
            throw new NotAcceptException("해당 예약은 이미 취소된 상태입니다.");
        }

        // 예약 상태를 취소로 변경한 후 저장
        book.setStatus(Status.CANCEL);
        bookRepository.save(book);

        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = book.getStartDate();
        LocalDateTime threeDaysBeforeStartDate = startDate.minusDays(3);

        // 환불 금액 계산
        int refundAmount;
        if (now.isAfter(threeDaysBeforeStartDate) && now.isBefore(startDate)) {
            // 현재 시간이 start_date 3일 전과 end_date 사이면 절반만 환불
            refundAmount = book.getTotalPrice() / 2;
        } else {
            refundAmount = book.getTotalPrice();
        }

        // user 의 cash 변동사항 저장
        return cashService.processTransaction(user, refundAmount, TransactionType.REFUND);

    }

    // 예약 조회 기능
    public List<BookInquiryResponse> inquiryBook(Integer userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("해당 ID의 사용자가 존재하지 않습니다."));

        List<BookEntity> books = bookRepository.findByUserId(userId);

        if (books.isEmpty()) {
            throw new NotFoundException("해당하는 예약이 존재하지 않습니다.");
        }

        // 각 BookEntity 를 BookInquiryResponse 로 변환하여 리스트로 반환
        return books.stream()
                .map(book -> {
                    // camp 의 여러 image 중 첫 번째 이미지를 선택
                    String firstImage = !book.getCamp().getImages().isEmpty()
                            ? book.getCamp().getImages().get(0).getImageUrl()
                            : null;

                    return BookInquiryResponse.of(
                            book.getId(),
                            book.getCamp().getName(),
                            firstImage,
                            book.getStartDate(),
                            book.getEndDate(),
                            book.getNum(),
                            book.getTotalPrice(),
                            book.getRequest(),
                            book.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }
}
