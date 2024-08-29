package com.github.project3.entity.admin;


import com.github.project3.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Admin")
public class AdminEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sales", nullable = false)
    private Integer sales;


    @Column(name = "updated_at", insertable = false, nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 스태틱 팩토리 메소드
    public static AdminEntity of(UserEntity user, Integer sales) {
        return AdminEntity.builder()
                .user(user)
                .sales(sales)
                .build();
    }
}
