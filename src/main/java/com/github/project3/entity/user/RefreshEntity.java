package com.github.project3.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RefreshToken")
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String loginId;

    @Column(nullable = false, length = 500)
    private String refresh;

    @Column(nullable = false, length = 40)
    private String expiration;
}
