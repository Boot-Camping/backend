package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampDescriptionEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampDescriptionDTO {
	private String description;

	// Entity -> DTO 변환
	public static CampDescriptionDTO fromEntity(CampDescriptionEntity descriptionEntity) {
		return CampDescriptionDTO.builder()
				.description(descriptionEntity.getDescription())
				.build();
	}

	// DTO -> Entity 변환
	public static CampDescriptionEntity toEntity(CampDescriptionDTO descriptionDTO) {
		return CampDescriptionEntity.builder()
				.description(descriptionDTO.getDescription())
				.build();
	}
}
