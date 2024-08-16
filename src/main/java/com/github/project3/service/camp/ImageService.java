package com.github.project3.service.camp;


import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

	/**
	 * 주어진 이미지 URL 목록을 기반으로 CampImageEntity 객체들의 리스트를 생성.
	 *
	 * @param imageUrls 이미지 URL 목록
	 * @param camp 캠핑지 엔티티 (연관 관계 설정을 위해 필요)
	 * @return 이미지 URL을 기반으로 생성된 CampImageEntity 객체들의 리스트
	 */
	public List<CampImageEntity> createImages(List<String> imageUrls, CampEntity camp) {
		return imageUrls.stream()
				.map(imageUrl -> CampImageEntity.builder()
						.imageUrl(imageUrl)
						.camp(camp) // 연관된 캠핑지 엔티티 설정
						.build())
				.collect(Collectors.toList());
	}
}
