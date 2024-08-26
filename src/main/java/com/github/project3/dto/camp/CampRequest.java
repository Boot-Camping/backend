package com.github.project3.dto.camp;

import com.github.project3.entity.camp.CampEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class CampRequest {
	private String name;
	private Integer price;
	private String addr;
	private String tel; // 추가된 전화번호 필드
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	private List<MultipartFile> imageFiles;
	private String description;
	private List<String> categories;

	public static CampEntity toEntity(CampRequest request) {
		return CampEntity.builder()
				.name(request.getName())
				.price(request.getPrice())
				.addr(request.getAddr())
				.tel(request.getTel()) // 전화번호 필드 추가
				.maxNum(request.getMaxNum())
				.standardNum(request.getStandardNum())
				.overCharge(request.getOverCharge())
				.build();
	}
}
