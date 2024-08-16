package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CategoryEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampCategoryDTO {

	private String name;

	// Entity -> DTO 변환
	public static CampCategoryDTO fromEntity(CategoryEntity categoryEntity) {
		return CampCategoryDTO.builder()
				.name(categoryEntity.getName())
				.build();
	}

	// DTO -> Entity 변환
	public static CategoryEntity toEntity(CampCategoryDTO categoryDTO) {
		return CategoryEntity.builder()
				.name(categoryDTO.getName())
				.build();
	}
}
