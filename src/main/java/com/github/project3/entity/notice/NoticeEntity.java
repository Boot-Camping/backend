package com.github.project3.entity.notice;

import com.github.project3.entity.review.ReviewEntity;
import com.github.project3.service.exceptions.NotFoundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="Notice")
public class NoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "notice", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImageEntity> images;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void update(String title, String description){
        if (title == null && description == null){
            throw new NotFoundException("수정사항을 입력해 주세요.");
        } else if (title == null) {
            this.setDescription(description);
        } else if (description == null) {
            this.setTitle(title);
        } else {
            this.setTitle(title);
            this.setDescription(description);
        }
    }
}
