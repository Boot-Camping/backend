package com.github.project3.service.camp;

import com.github.project3.dto.camp.CampImageDTO;
import com.github.project3.entity.camp.CampImageEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

	/**
	 * 주어진 이미지 URL 목록을 기반으로 CampImageEntity 객체들의 리스트를 생성.
	 *
	 * @param imageUrls 이미지 URL 목록
	 * @return 이미지 URL을 기반으로 생성된 CampImageEntity 객체들의 리스트
	 */
	public List<CampImageEntity> createImages(List<String> imageUrls) {
		// 이미지 URL 목록을 스트림으로 처리하여 각 URL을 CampImageEntity로 변환.
		return imageUrls.stream()
				// 각 URL을 CampImageDTO로 변환한 뒤, 엔티티로 변환.
				.map(imageUrl -> CampImageDTO.toEntity(CampImageDTO.builder().imageUrl(imageUrl).build()))
				// 최종적으로 엔티티 리스트로 수집합니다.
				.toList();
	}
}
