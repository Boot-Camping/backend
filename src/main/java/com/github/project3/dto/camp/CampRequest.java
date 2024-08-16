package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampRequest {
	private String name;
	private Integer price;
	private String addr;
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	private List<String> imageUrls;
	private String description;
	private List<String> categories;

	public static CampEntity toEntity(CampRequest request) {
		return CampEntity.builder()
				.name(request.getName())
				.price(request.getPrice())
				.addr(request.getAddr())
				.maxNum(request.getMaxNum())
				.standardNum(request.getStandardNum())
				.overCharge(request.getOverCharge())
				.build();
	}
}
