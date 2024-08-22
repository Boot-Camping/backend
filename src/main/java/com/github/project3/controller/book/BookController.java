package com.github.project3.controller.book;

import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 컨트롤러로 예약 관련 요청을 처리합니다.
 * 예약 등록, 취소, 조회 기능을 제공합니다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    /**
     * 캠핑장 예약을 등록합니다.
     *
     * @param campId                예약하려는 캠핑장의 ID
     * @param userId                예약을 요청하는 사용자의 ID
     * @param bookRegisterRequest   예약 등록 요청 정보를 담고 있는 객체
     * @return 예약이 성공적으로 완료되었을 때 "예약이 완료되었습니다." 메시지를 포함한 ResponseEntity 반환
     */
    @PostMapping("/{campId}/{userId}")
    public ResponseEntity<String> registerBook(@PathVariable Integer campId, @PathVariable Integer userId,
                                        @RequestBody BookRegisterRequest bookRegisterRequest){
        bookService.registerBook(campId, userId, bookRegisterRequest);
        return ResponseEntity.ok("예약이 완료되었습니다.");
    }

    /**
     * 캠핑장 예약을 취소합니다.
     *
     * @param bookId  취소하려는 예약의 ID
     * @param userId  예약 취소를 요청하는 사용자의 ID
     * @return 예약이 성공적으로 취소되었을 때 "예약이 취소되었습니다."와 환불 금액을 포함한 ResponseEntity 반환
     */
    @PutMapping("/{bookId}/{userId}")
    public ResponseEntity<String> cancelBook(@PathVariable Integer bookId, @PathVariable Integer userId){
        Integer cash = bookService.cancelBook(bookId, userId);
        return ResponseEntity.ok("예약이 취소되었습니다." + cash + " 원이 환불되었습니다.");
    }

    /**
     * 사용자의 예약 내역을 조회합니다.
     *
     * @param userId  예약 내역을 조회하려는 사용자의 ID
     * @return 예약 내역 리스트를 포함한 ResponseEntity 반환
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<BookInquiryResponse>> inquiryBook(@PathVariable Integer userId){
        List<BookInquiryResponse> response = bookService.inquiryBook(userId);
        return ResponseEntity.ok(response);
    }
}
