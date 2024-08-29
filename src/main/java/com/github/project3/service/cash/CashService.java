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

    /**
     * 사용자의 현재 잔액을 가져오는 메서드
     *
     * @param user 잔액을 확인할 사용자 엔티티
     * @return 사용자의 현재 잔액. 잔액이 없는 경우 0을 반환
     */
    public Integer getCurrentBalance(UserEntity user) {
        return user.getCash().stream()
                .max(Comparator.comparing(CashEntity::getTransactionDate))
                .map(CashEntity::getBalanceAfterTransaction)
                .orElse(0);
    }

    /**
     * 유형에 따라 사용자의 잔액을 갱신하고, 해당 거래를 저장하는 메서드
     *
     * @param user             거래를 진행할 사용자 엔티티
     * @param amount           거래 금액
     * @param transactionType  거래 유형 (결제, 환불, 리뷰 보상, 충전)
     * @return 거래 금액
     */
    public Integer processTransaction(UserEntity user, Integer amount, TransactionType transactionType) {
        Integer currentBalance = getCurrentBalance(user);

        Integer newBalance = calculateNewBalance(currentBalance, amount, transactionType);

        CashEntity cashTransaction = CashEntity.of(
                user,
                amount,
                transactionType,
                newBalance
        );
        cashRepository.save(cashTransaction);

        return amount;
    }

    /**
     * 처리 후 고객의 남은 잔액을 계산하는 메서드
     *
     * @param currentBalance  현재 사용자의 잔액
     * @param amount          거래 금액
     * @param transactionType 거래 유형 (결제, 환불, 리뷰 보상, 충전)
     * @return 계산된 새로운 잔액
     * @throws NotAcceptException       사용자의 잔액이 부족하여 결제가 취소될 때 예외 발생
     * @throws IllegalArgumentException 지원하지 않는 거래 유형일 경우 예외 발생
     */
    private Integer calculateNewBalance(Integer currentBalance, Integer amount, TransactionType transactionType){
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
        return newBalance;
    }
}

