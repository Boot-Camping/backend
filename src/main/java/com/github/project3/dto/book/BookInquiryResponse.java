package com.github.project3.dto.book;

import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookInquiryResponse {
    private Integer bookId;
    private String campName;
    private String imgUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer bookNum;
    private Integer totalPrice;
    private String bookRequest;
    private Status bookStatus;

    // 스태틱 팩토리 메소드
    public static BookInquiryResponse of(Integer bookId, String campName, String imgUrl, LocalDateTime startDate, LocalDateTime endDate, Integer bookNum, Integer totalPrice, String bookRequest, Status bookStatus) {
        return BookInquiryResponse.builder()
                .bookId(bookId)
                .campName(campName)
                .imgUrl(imgUrl)
                .startDate(startDate)
                .endDate(endDate)
                .bookNum(bookNum)
                .totalPrice(totalPrice)
                .bookRequest(bookRequest)
                .bookStatus(bookStatus)
                .build();
    }
}
