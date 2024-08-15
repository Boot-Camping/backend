package com.github.project3.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.print.Book;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRegisterRequest {

    private Integer totalPrice;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String bookRequest;
    private Integer bookNum;

    // 스태틱 팩토리 메소드
    public static BookRegisterRequest of(Integer totalPrice, LocalDateTime checkIn, LocalDateTime checkOut, String bookRequest, Integer bookNum) {
        return BookRegisterRequest.builder()
                .totalPrice(totalPrice)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .bookRequest(bookRequest)
                .bookNum(bookNum)
                .build();
    }
}
