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
}
