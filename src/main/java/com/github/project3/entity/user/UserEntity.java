package com.github.project3.entity.user;

import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.entity.user.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="User")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "tel")
    private String tel;

    @Column(name = "addr", nullable = false)
    private String addr;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    private List<UserImageEntity> images;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CashEntity> cash;

    @PrePersist
    protected void onCreate() {
        if (this.role == null) {
            this.role = Role.GENERAL;
        }
        if (this.status == null) {
            this.status = Status.ACTIVE;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
