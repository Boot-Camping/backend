package com.github.project3.service.book;

import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.BookDateEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.repository.book.BookRepository;
import com.github.project3.repository.bookDate.BookDateRepository;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.service.cash.CashService;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import com.github.project3.service.user.UserService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final UserService userService;
    private final CashService cashService;
    private final BookRepository bookRepository;
    private final BookDateRepository bookDateRepository;
    private final CampRepository campRepository;

    /**
     * 캠핑장 예약을 등록합니다.
     *
     * @param campId                예약하려는 캠핑장의 ID
     * @param bookRegisterRequest   예약 등록 요청 정보를 담고 있는 객체
     * @throws NotFoundException    해당 ID의 사용자 또는 캠핑장이 존재하지 않을 경우 발생
     * @throws NotAcceptException   요청된 날짜에 이미 예약이 존재할 경우 발생
     */
    @Transactional
    @Retryable(
            value = OptimisticLockException.class, // 재시도할 예외
            maxAttempts = 3, // 최대 재시도 횟수
            backoff = @Backoff(delay = 1000) // 재시도 간격(1초)
    )
    public void registerBook(Integer campId, BookRegisterRequest bookRegisterRequest) {

        UserEntity user = getAuthenticatedUser();
        log.info("인증된 유저의 ID : {}", user.getId());

        CampEntity camp = getCampEntityById(campId);

        LocalDateTime requestCheckIn = bookRegisterRequest.getCheckIn();
        LocalDateTime requestCheckOut = bookRegisterRequest.getCheckOut();

        validateBookingDates(camp, requestCheckIn, requestCheckOut);

        int totalPrice = calculateTotalPriceWithDiscount(bookRegisterRequest.getTotalPrice(), requestCheckIn);
        processUserPayment(user, totalPrice);

        BookEntity savedBook = saveBooking(user, camp, totalPrice, bookRegisterRequest);

        saveBookDates(savedBook, requestCheckIn, requestCheckOut);
    }

    // Retry 요청(3번) 실행 후 정상적으로 등록이 안되면 실행되는 메서드
    @Recover
    public void recover(OptimisticLockException e, Integer campId, BookRegisterRequest bookRegisterRequest) {
        log.error("예약 등록 중 지속적으로 등록 실패 한 campId: {}", campId, e);
        throw new NotAcceptException("다른 사용자가 동일한 캠핑장을 예약하고 있습니다. 다시 시도해 주세요.");
    }

    /**
     * 캠핑장 예약을 취소합니다.
     *
     * @param bookId  취소하려는 예약의 ID
     * @return 환불 금액
     * @throws NotFoundException    해당 예약 또는 사용자가 존재하지 않을 경우 발생
     * @throws NotAcceptException   예약이 이미 취소된 상태이거나 기타 조건에 따라 발생
     */
    @Transactional
    public Integer cancelBook(Integer bookId) {

        BookEntity book = getBookEntityById(bookId);

        validateCancel(book);

        book.setStatus(Status.CANCEL);
        bookRepository.save(book);

        UserEntity user = getAuthenticatedUser();
        return processUserRefund(user, book);
    }

    /**
     * 사용자의 예약 내역을 조회합니다.
     *
     * @return 예약 내역 리스트
     * @throws NotFoundException    해당 사용자가 존재하지 않거나 예약이 없을 경우 발생
     */
    public List<BookInquiryResponse> getBooks() {
        UserEntity user = getAuthenticatedUser();

        List<BookInquiryResponse> books = bookRepository.findBookInquiriesByUserId(user.getId());

        if (books.isEmpty()) {
            throw new NotFoundException("해당하는 예약이 존재하지 않습니다.");
        }

        return books;
    }

    // 사용자 조회 메서드
    private UserEntity getAuthenticatedUser() {
        return userService.findAuthenticatedUser();
    }

    // 캠프 조회 메서드
    private CampEntity getCampEntityById(Integer campId) {
        return campRepository.findById(campId)
                .orElseThrow(() -> new NotFoundException("해당하는 캠핑지가 존재하지 않습니다."));
    }

    // 예약 날짜 중복 확인 메서드
    private void validateBookingDates(CampEntity camp, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Status> statuses = Arrays.asList(Status.BOOKING, Status.DECIDE);
        if (isDateConflict(camp, checkIn, checkOut, statuses)) {
            throw new NotAcceptException("해당 날짜에 이미 예약이 존재합니다. 다른 날짜를 선택해주세요.");
        }
    }

    // 예약 날짜 중복 확인 메서드
    private boolean isDateConflict(CampEntity camp, LocalDateTime checkIn, LocalDateTime checkOut, List<Status> statuses) {
        return bookRepository.existsByCampAndDateRangeOverlap(camp, checkIn, checkOut, statuses);
    }

    // 예약 등록 시 할인 금액 계산 메서드
    private int calculateTotalPriceWithDiscount(Integer totalPrice, LocalDateTime requestCheckIn) {
        if (ChronoUnit.DAYS.between(LocalDateTime.now(), requestCheckIn) <= 2) {
            totalPrice -= 10000;
        }
        return totalPrice;
    }

    // 에약 시 결제 메서드
    private void processUserPayment(UserEntity user, int amount) {
        cashService.processTransaction(user, amount, TransactionType.PAYMENT);
    }

   // 예약 정보 저장 메서드
    private BookEntity saveBooking(UserEntity user, CampEntity camp, int totalPrice, BookRegisterRequest request) {
        BookEntity book = BookEntity.of(
                user,
                camp,
                totalPrice,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getBookRequest(),
                request.getBookNum(),
                Status.BOOKING
        );
        return bookRepository.save(book);
    }

    // 예약 조회 메서드
    private BookEntity getBookEntityById(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("해당하는 예약이 존재하지 않습니다."));
    }

    // 예약 취소 검증 메서드
    private void validateCancel(BookEntity book) {
        if (book.getStatus() == Status.CANCEL) {
            throw new NotAcceptException("해당 예약은 이미 취소된 상태입니다.");
        }
    }

    // 예약 취소 시 환불 메서드
    private int processUserRefund(UserEntity user, BookEntity book) {
        int refundAmount = calculateRefundAmount(book);
        return cashService.processTransaction(user, refundAmount, TransactionType.REFUND);
    }

    // 예약 취소 시 환불 금액 계산 메서드
    private int calculateRefundAmount(BookEntity book) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = book.getStartDate();
        LocalDateTime threeDaysBeforeStartDate = startDate.minusDays(3);

        if (now.isAfter(threeDaysBeforeStartDate) && now.isBefore(startDate)) {
            return book.getTotalPrice() / 2; // 3일 이내 취소 시 50% 환불
        } else {
            return book.getTotalPrice();
        }
    }

    // 예약 날짜 저장 메서드
    private void saveBookDates(BookEntity savedBook, LocalDateTime requestCheckIn, LocalDateTime requestCheckOut) {

        List<BookDateEntity> bookDates = new ArrayList<>();

        // 반복문으로 체크인날짜, 체크아웃날짜, 중간에 있는 날짜들도 DB에 저장
        while (!requestCheckIn.isAfter(requestCheckOut)) {
            BookDateEntity bookDate = new BookDateEntity();
            bookDate.setBook(savedBook);
            bookDate.setDate(requestCheckIn);
            bookDates.add(bookDate);
            requestCheckIn = requestCheckIn.plusDays(1);
        }
        bookDateRepository.saveAll(bookDates);
    }
}
