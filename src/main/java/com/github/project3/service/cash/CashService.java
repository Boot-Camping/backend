package com.github.project3.service.cash;

import com.github.project3.entity.user.CashEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.TransactionType;
import com.github.project3.repository.cash.CashRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashRepository cashRepository;

    // user 의 현재 잔액을 가져오는 로직
    public Integer getCurrentBalance(UserEntity user) {
        return user.getCash().stream()
                .max(Comparator.comparing(CashEntity::getTransactionDate))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);
    }

    // switch-case 문으로 transactionType 별 저장
    public void processTransaction(UserEntity user, Integer amount, TransactionType transactionType) {
        Integer currentBalance = getCurrentBalance(user);
        Integer newBalance;

        switch (transactionType) {
            case PAYMENT:
                newBalance = currentBalance - amount;
                if (newBalance < 0) {
                    throw new NotAcceptException("고객님의 현재 잔액은 " + currentBalance + "원 입니다. 결제가 취소됩니다.");
                }
                break;

            case REFUND:
                newBalance = currentBalance + amount;
                break;

            case REWARD:
                newBalance = currentBalance + amount;
                break;

            case DEPOSIT:
                newBalance = currentBalance + amount;
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 transaction type : " + transactionType + " 입니다.");
        }

        CashEntity cashTransaction = CashEntity.of(
                user,
                amount,
                transactionType,
                newBalance
        );
        cashRepository.save(cashTransaction);
    }
}