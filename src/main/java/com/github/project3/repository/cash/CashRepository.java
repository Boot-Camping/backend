package com.github.project3.repository.cash;

import com.github.project3.dto.mypage.CashTransactionResponse;
import com.github.project3.entity.user.CashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashRepository extends JpaRepository<CashEntity, Integer> {

    List<CashEntity> findByUserId(Integer userId);

    // campName 을 같이 return 하기 위한 JPQL
    @Query("SELECT new com.github.project3.dto.mypage.CashTransactionResponse(ce.id, ce.transactionDate, ce.transactionType, ce.amount, ce.balanceAfterTransaction, ca.name) " +
            "FROM CashEntity ce " +
            "LEFT JOIN BookEntity be ON ce.transactionDate = be.createdAt AND ce.user.id = be.user.id " +
            "LEFT JOIN CampEntity ca ON be.camp.id = ca.id " +
            "WHERE ce.user.id = :userId " +
            "AND (ce.transactionType <> 'PAYMENT' OR (ce.transactionType = 'PAYMENT' AND be.id IS NOT NULL))")
    List<CashTransactionResponse> findCashTransactionsWithCampNameByUserId(@Param("userId") Integer userId);
}
