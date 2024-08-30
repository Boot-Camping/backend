package com.github.project3.entity.book;

import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.book.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="Book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "camp_id", nullable = false)
    private CampEntity camp;

    @Column(name = "num", nullable = false)
    private Integer num;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "Request", nullable = false, columnDefinition = "TEXT")
    private String request;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Version
    @Column(name = "version")
    private Integer version;

    // 스태틱 팩토리 메소드
    public static BookEntity of(UserEntity user, CampEntity camp, Integer totalPrice, LocalDateTime startDate, LocalDateTime endDate, String request, Integer num, Status status) {
        return BookEntity.builder()
                .user(user)
                .camp(camp)
                .totalPrice(totalPrice)
                .startDate(startDate)
                .endDate(endDate)
                .request(request)
                .num(num)
                .status(status)
                .build();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = Status.BOOKING;
        }
    }
}
