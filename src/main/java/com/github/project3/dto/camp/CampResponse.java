package com.github.project3.dto.camp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CampResponse {

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
	private LocalDateTime updatedAt; // 추가된 필드

	private List<String> imageUrls;
	private String description;
	private List<String> categories;
	private Double averageGrade; // 평점 필드 추가
	private Long reviewCount;  // 리뷰수 필드 추가

	private Long reservedDateCount; // 예약된 날짜 수 필드 추가


	// 기본값을 사용하는 메서드
	public static CampResponse fromEntity(CampEntity campEntity) {
		return fromEntity(campEntity, 0.0, 0L, 0L);
	}

	// 스태틱 팩토리 메서드를 사용해 엔티티를 DTO로 변환
	public static CampResponse fromEntity(CampEntity campEntity, Double averageGrade, Long reviewCount, Long reservedDateCount) {
		return CampResponse.builder()
				.id(campEntity.getId())
				.name(campEntity.getName())
				.price(campEntity.getPrice())
				.addr(campEntity.getAddr())
				.tel(campEntity.getTel()) // 전화번호 필드 추가
				.maxNum(campEntity.getMaxNum())
				.standardNum(campEntity.getStandardNum())
				.overCharge(campEntity.getOverCharge())
				.createdAt(campEntity.getCreatedAt())
				.updatedAt(campEntity.getUpdatedAt()) // 추가된 필드
				.imageUrls(campEntity.getImages().stream()
						.map(CampImageEntity::getImageUrl)
						.collect(Collectors.toList()))
				.description(campEntity.getDescription() != null ? campEntity.getDescription().getDescription() : "")
				.categories(campEntity.getCampCategories().stream()
						.map(campCategory -> campCategory.getCategory().getName())
						.collect(Collectors.toList()))
				.averageGrade(averageGrade != null ? averageGrade : 0.0) // 평점이 없는 경우 0.0 반환
				.reviewCount(reviewCount != null ? reviewCount : 0L) // 리뷰수가 없는 경우 0L 반환
				.reservedDateCount(reservedDateCount != null ? reservedDateCount : 0L) // 예약된 날짜 수 추가
				.build();
	}
}

