package com.github.project3.entity.camp;


import jakarta.persistence.*;
import lombok.*;
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

    @OneToOne(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    private CampDescriptionEntity description;

    @Builder.Default
    @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampImageEntity> images = new ArrayList<>();

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "CampCategory",
        joinColumns = @JoinColumn(name = "camp_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();



    public void addImages(List<CampImageEntity> images) {
        this.images.addAll(images);
        images.forEach(image -> image.setCamp(this));
    }

    public void setDescription(CampDescriptionEntity description) {
        this.description = description;
        description.setCamp(this);
    }

    public void addCategories(List<CategoryEntity> categories) {
        this.categories.addAll(categories);
    }
}