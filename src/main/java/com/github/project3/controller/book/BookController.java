package com.github.project3.controller.book;

import com.github.project3.dto.book.BookCancelResponse;
import com.github.project3.dto.book.BookRegisterRequest;
import com.github.project3.dto.mypage.UserProfileResponse;
import com.github.project3.service.book.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<BookCancelResponse> cancelBook(@PathVariable Integer bookId, @PathVariable Integer userId){
        BookCancelResponse response = bookService.cancelBook(bookId, userId);
        return ResponseEntity.ok(response);
    }
}
