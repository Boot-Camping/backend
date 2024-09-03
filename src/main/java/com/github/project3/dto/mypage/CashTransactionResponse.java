package com.github.project3.dto.mypage;

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
}
