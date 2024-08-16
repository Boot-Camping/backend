package com.github.project3.entity.camp;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Camp")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "addr")
    private String addr;

    @Column(name = "max_num")
    private Integer maxNum;

    @Column(name = "standard_num")
    private Integer standardNum;

    @Column(name = "over_charge")
    private Integer overCharge;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    private CampDescriptionEntity description;

    @Builder.Default
    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampImageEntity> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampCategoryEntity> campCategories = new ArrayList<>();



    public void addImages(List<CampImageEntity> images) {
        this.images.addAll(images);
        images.forEach(image -> image.setCamp(this));
    }

    // 이미지 업데이트 메서드
    public void updateImages(List<CampImageEntity> newImages) {
        // 기존 이미지 제거
        this.images.clear();

        // 새로운 이미지 추가
        newImages.forEach(image -> {
            image.setCamp(this); // 이미지의 캠핑지 설정
            this.images.add(image);
        });
    }

    public void setDescription(CampDescriptionEntity description) {
        this.description = description;
        description.setCamp(this);
    }

    public void addCategories(List<CategoryEntity> categories) {
        // 기존 카테고리 연결 제거
        this.campCategories.clear();

        // 새로운 카테고리 연결 추가
        categories.forEach(category -> {
            CampCategoryEntity campCategory = new CampCategoryEntity();
            campCategory.setCamp(this);
            campCategory.setCategory(category);
            this.campCategories.add(campCategory);
        });
    }
}