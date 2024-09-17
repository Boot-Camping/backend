package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CampDataDTO {
	private CampEntity campEntity;
	private Double averageGrade;
	private Long reviewCount;
	private Long reservedDateCount;
}
