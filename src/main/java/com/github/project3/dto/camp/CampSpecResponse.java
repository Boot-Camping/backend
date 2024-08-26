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
	private String tel; // 추가된 전화번호 필드
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
	private List<String> imageUrls;
	private String description;
	private List<String> categories;
	private Integer viewCount; // 조회수 필드 추가
	private Double averageGrade; // 평점 필드 추가

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private List<LocalDateTime> reservedDates;

	// 스태틱 팩토리 메서드
	public static CampSpecResponse fromEntity(CampEntity campEntity, List<LocalDateTime> reservedDates, Integer viewCount, Double averageGrade) {
		return CampSpecResponse.builder()
				.id(campEntity.getId())
				.name(campEntity.getName())
				.price(campEntity.getPrice())
				.addr(campEntity.getAddr())
				.tel(campEntity.getTel()) // 전화번호 필드 추가
				.maxNum(campEntity.getMaxNum())
				.standardNum(campEntity.getStandardNum())
				.overCharge(campEntity.getOverCharge())
				.createdAt(campEntity.getCreatedAt())
				.updatedAt(campEntity.getUpdatedAt())
				.imageUrls(campEntity.getImages().stream()
						.map(CampImageEntity::getImageUrl)
						.collect(Collectors.toList()))
				.description(campEntity.getDescription() != null ? campEntity.getDescription().getDescription() : "")
				.categories(campEntity.getCampCategories().stream()
						.map(campCategory -> campCategory.getCategory().getName())
						.collect(Collectors.toList()))
				.viewCount(viewCount != null ? viewCount : 0) // 조회수가 없는 경우 0 반환
				.averageGrade(averageGrade != null ? averageGrade : 0.0) // 평점이 없는 경우 0.0 반환
				.reservedDates(reservedDates)
				.build();
	}
}
