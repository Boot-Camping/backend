package com.github.project3.entity.camp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ViewCount")
public class ViewCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "camp_id", nullable = false)
    private CampEntity camp;

    @Column(name = "count", nullable = false)
    private Integer count;

    @PrePersist
    protected void onCreate() {
        if (this.count == null) {
            this.count = 0;
        }
    }

    // 스태틱 팩토리 메서드
    public static ViewCountEntity createWithInitialCount(CampEntity camp) {
        return ViewCountEntity.builder()
            .camp(camp)
            .count(0)
            .build();
    }

    // 조회수 증가 메서드
    public void incrementCount() {
        this.count++;
    }
}
