package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CampResponse {

	private Integer id;
	private String name;
	private Integer price;
	private String addr;
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	private LocalDateTime createdAt;
	private List<String> imageUrls;
	private String description;
	private List<String> categories;

	// 스태틱 팩토리 메서드를 사용해 엔티티를 DTO로 변환
	public static CampResponse fromEntity(CampEntity campEntity) {
		return CampResponse.builder()
				.id(campEntity.getId())
				.name(campEntity.getName())
				.price(campEntity.getPrice())
				.addr(campEntity.getAddr())
				.maxNum(campEntity.getMaxNum())
				.standardNum(campEntity.getStandardNum())
				.overCharge(campEntity.getOverCharge())
				.imageUrls(campEntity.getImages().stream()
						.map(CampImageEntity::getImageUrl)
						.collect(Collectors.toList()))
				.description(campEntity.getDescription() != null ? campEntity.getDescription().getDescription() : "")
				.categories(campEntity.getCampCategories().stream()
						.map(campCategory -> campCategory.getCategory().getName())
						.collect(Collectors.toList()))
				.build();
	}
}

