package com.github.project3.controller.book;

import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 컨트롤러로 예약 관련 요청을 처리합니다.
 * 예약 등록, 취소, 조회 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/camps/bookings")
public class BookController {

    private final BookService bookService;

    /**
     * 캠핑장 예약을 등록합니다.
     *
     * @param campId                예약하려는 캠핑장의 ID
     * @param bookRegisterRequest   예약 등록 요청 정보를 담고 있는 객체
     * @return 예약이 성공적으로 완료되었을 때 "예약이 완료되었습니다." 메시지를 포함한 ResponseEntity 반환
     */
    @Operation(summary = "예약 등록", description = "캠핑장 예약을 등록합니다.")
    @PostMapping("/{campId}")
    public ResponseEntity<String> registerBook(@PathVariable Integer campId,
                                        @RequestBody BookRegisterRequest bookRegisterRequest){
        bookService.registerBook(campId, bookRegisterRequest);
        return ResponseEntity.ok("예약이 완료되었습니다.");
    }

    /**
     * 캠핑장 예약을 취소합니다.
     *
     * @param bookId  취소하려는 예약의 ID
     * @return 예약이 성공적으로 취소되었을 때 "예약이 취소되었습니다."와 환불 금액을 포함한 ResponseEntity 반환
     */
    @Operation(summary = "예약 취소", description = "기존 캠핑장 예약을 취소합니다.")
    @PutMapping("/{bookId}")
    public ResponseEntity<String> cancelBook(@PathVariable Integer bookId){
        Integer cash = bookService.cancelBook(bookId);
        return ResponseEntity.ok("예약이 취소되었습니다." + cash + " 원이 환불되었습니다.");
    }

    /**
     * 사용자의 예약 내역을 조회합니다.
     *
     * @return 예약 내역 리스트를 포함한 ResponseEntity 반환
     */
    @Operation(summary = "예약 조회", description = "사용자의 캠핑장 예약 내역을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BookInquiryResponse>> getBooks(){
        List<BookInquiryResponse> response = bookService.getBooks();
        return ResponseEntity.ok(response);
    }
}
