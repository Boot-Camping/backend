package com.github.project3.dto.camp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampSpecResponse {

	private Integer id;
	private String name;
	private Integer price;
	private String addr;
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<String> imageUrls;
	private String description;
	private List<String> categories;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private List<LocalDateTime> reservedDates;

	// 스태틱 팩토리 메서드
	public static CampSpecResponse fromEntity(CampEntity campEntity, List<LocalDateTime> reservedDates) {
		return CampSpecResponse.builder()
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
				.reservedDates(reservedDates)
				.build();
	}
}
