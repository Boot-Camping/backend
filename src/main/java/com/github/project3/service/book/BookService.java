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
import java.util.stream.Collectors;

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

            // 인증이 완료되어 SecurityContextHolder 저장된 user 의 id로 검색
            UserEntity user = userService.findAuthenticatedUser();
            log.info("인증된 유저의 ID : {}", user.getId());

            CampEntity camp = campRepository.findById(campId).orElseThrow(() -> new NotFoundException("해당하는 캠핑지가 존재하지 않습니다."));

            LocalDateTime requestCheckIn = bookRegisterRequest.getCheckIn();
            LocalDateTime requestCheckOut = bookRegisterRequest.getCheckOut();

            List<Status> statuses = Arrays.asList(Status.BOOKING, Status.DECIDE);

            // 예약 날짜 중복 확인
            if (isDateConflict(camp, requestCheckIn, requestCheckOut, statuses)) {
                throw new NotAcceptException("해당 날짜에 이미 예약이 존재합니다. 다른 날짜를 선택해주세요.");
            }

            // user 의 cash 변동사항 저장
            int totalPrice = calculateTotalPrice(bookRegisterRequest.getTotalPrice(), requestCheckIn);
            cashService.processTransaction(user, totalPrice, TransactionType.PAYMENT);

            // 예약 정보 등록
            BookEntity book = BookEntity.of(
                    user,
                    camp,
                    totalPrice,
                    bookRegisterRequest.getCheckIn(),
                    bookRegisterRequest.getCheckOut(),
                    bookRegisterRequest.getBookRequest(),
                    bookRegisterRequest.getBookNum(),
                    Status.BOOKING
            );

            BookEntity savedBook = bookRepository.save(book);

            // CheckIn 부터 CheckOut 까지의 날짜를 모두 저장
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

        BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("해당하는 예약이 존재하지 않습니다."));

        if (book.getStatus() == Status.CANCEL) {
            throw new NotAcceptException("해당 예약은 이미 취소된 상태입니다.");
        }

        // 예약 상태를 취소로 변경한 후 저장
        book.setStatus(Status.CANCEL);
        bookRepository.save(book);

        // user 의 cash 변동사항 저장
        UserEntity user = userService.findAuthenticatedUser();
        return cashService.processTransaction(user, calculateRefundAmount(book), TransactionType.REFUND);
    }

    /**
     * 사용자의 예약 내역을 조회합니다.
     *
     * @return 예약 내역 리스트
     * @throws NotFoundException    해당 사용자가 존재하지 않거나 예약이 없을 경우 발생
     */
    public List<BookInquiryResponse> inquiryBook() {
        UserEntity user = userService.findAuthenticatedUser();

        List<BookEntity> books = bookRepository.findByUserId(user.getId());

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
                            book.getCamp().getId(),
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

    /**
     * 예약 날짜가 중복되는지 확인하는 메서드입니다.
     *
     * @param camp      예약하려는 캠핑장 엔티티
     * @param checkIn   예약 시작 날짜
     * @param checkOut  예약 종료 날짜
     * @param statuses  예약 상태 리스트
     * @return 예약 날짜가 중복되는 경우 true, 그렇지 않으면 false
     */
    private boolean isDateConflict(CampEntity camp, LocalDateTime checkIn, LocalDateTime checkOut, List<Status> statuses) {
        return bookRepository.existsByCampAndDateRangeOverlap(camp, checkIn, checkOut, statuses);
    }

    /**
     * 예약 등록 시 할인 금액을 계산하는 메서드입니다.
     *
     * @param totalPrice    예약 총 금액
     * @param requestCheckIn 예약 시작 날짜
     * @return 할인 적용 후의 총 금액
     */
    private int calculateTotalPrice(Integer totalPrice, LocalDateTime requestCheckIn) {

        // 예약 날짜에 임박하면(2일 이내) 예약금 10,000원 할인
        if (ChronoUnit.DAYS.between(LocalDateTime.now(), requestCheckIn) <= 2) {
            totalPrice -= 10000;
        }
        return totalPrice;
    }

    /**
     * 예약된 날짜를 저장하는 메서드입니다.
     *
     * @param savedBook       저장된 예약 엔티티
     * @param requestCheckIn  예약 시작 날짜
     * @param requestCheckOut 예약 종료 날짜
     */
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

    /**
     * 예약 취소 시 환불 금액을 계산하는 메서드입니다.
     *
     * @param book 예약 엔티티
     * @return 계산된 환불 금액
     */
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
}
