package com.github.project3.controller.book;

import com.github.project3.dto.book.BookCancelResponse;
import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;

    @PostMapping("/{campId}/{userId}")
    public ResponseEntity<String> registerBook(@PathVariable Integer campId, @PathVariable Integer userId,
                                        @RequestBody BookRegisterRequest bookRegisterRequest){
        bookService.registerBook(campId, userId, bookRegisterRequest);
        return ResponseEntity.ok("예약이 완료되었습니다.");
    }

    @PutMapping("/{bookId}/{userId}")
    public ResponseEntity<String> cancelBook(@PathVariable Integer bookId, @PathVariable Integer userId){
        bookService.cancelBook(bookId, userId);
        return ResponseEntity.ok("예약이 취소되었습니다.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<BookInquiryResponse>> inquiryBook(@PathVariable Integer userId){
        List<BookInquiryResponse> response = bookService.inquiryBook(userId);
        return ResponseEntity.ok(response);
    }
}
