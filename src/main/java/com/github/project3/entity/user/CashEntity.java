package com.github.project3.entity.user;

import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="Cash")
public class CashEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    // 결제 후 잔액
    @Column(name = "balance_after_transaction", nullable = false)
    private Integer balanceAfterTransaction;

    @PrePersist
    protected void onCreate() {
        if (this.transactionDate == null) {
            this.transactionDate = LocalDateTime.now();
        }
    }

    // 스태틱 팩토리 메소드
    public static CashEntity of(UserEntity user, Integer amount, TransactionType transactionType, Integer balanceAfterTransaction) {
        return CashEntity.builder()
                .user(user)
                .amount(amount)
                .transactionType(transactionType)
                .balanceAfterTransaction(balanceAfterTransaction)
                .build();
    }
}
