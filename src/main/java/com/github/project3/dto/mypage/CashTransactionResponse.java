package com.github.project3.dto.mypage;

import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashTransactionResponse {
    private Integer transactionId;
    private LocalDateTime transactionDate;
    private TransactionType transactionType;
    private Integer beforeTransactionCash;
    private Integer afterTransactionCash;

    // 스태틱 팩토리 메서드
    public static CashTransactionResponse from(CashEntity cash) {
        CashTransactionResponse response = new CashTransactionResponse();
        response.transactionId = cash.getId();
        response.transactionDate = cash.getTransactionDate();
        response.transactionType = cash.getTransactionType();
        response.beforeTransactionCash = cash.getAmount();
        response.afterTransactionCash = cash.getBalanceAfterTransaction();
        return response;
    }
}
