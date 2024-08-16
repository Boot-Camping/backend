package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampImageEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampImageDTO {
	private String imageUrl;

	// Entity -> DTO 변환
	public static CampImageDTO fromEntity(CampImageEntity imageEntity) {
		return CampImageDTO.builder()
				.imageUrl(imageEntity.getImageUrl())
				.build();
	}

	// DTO -> Entity 변환
	public static CampImageEntity toEntity(CampImageDTO imageDTO) {
		return CampImageEntity.builder()
				.imageUrl(imageDTO.getImageUrl())
				.build();
	}
}
