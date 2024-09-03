package com.github.project3.dto.mypage;

import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.user.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashTransactionResponse {
    private Integer transactionId;
    private LocalDateTime transactionDate;
    private TransactionType transactionType;
    private Integer beforeTransactionCash;
    private Integer afterTransactionCash;
    private String campName;

    // 스태틱 팩토리 메서드
    public static CashTransactionResponse of(Integer transactionId, LocalDateTime transactionDate, TransactionType transactionType, Integer beforeTransactionCash, Integer afterTransactionCash, String campName) {
        return CashTransactionResponse.builder()
                .transactionId(transactionId)
                .transactionDate(transactionDate)
                .transactionType(transactionType)
                .beforeTransactionCash(beforeTransactionCash)
                .afterTransactionCash(afterTransactionCash)
                .campName(campName)
                .build();
    }
}
