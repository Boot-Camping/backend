package com.github.project3.entity.camp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CampDescription")
public class CampDescriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_id")
    private CampEntity camp;


}
