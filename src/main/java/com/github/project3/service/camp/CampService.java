package com.github.project3.service.camp;

import com.github.project3.dto.camp.*;
import com.github.project3.entity.camp.CampDescriptionEntity;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import com.github.project3.entity.camp.CategoryEntity;
import com.github.project3.repository.camp.CampRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CampService {

	private final CampRepository campRepository;
	private final DescriptionService descriptionService;
	private final ImageService imageService;
	private final CategoryService categoryService;

	/**
	 * 캠핑지 등록 요청을 처리.
	 * 주어진 DTO 데이터를 바탕으로 캠핑지 엔티티를 생성한 후 데이터베이스에 저장.
	 *
	 * @param campRequestDTO 클라이언트로부터 받은 캠핑지 등록 요청 DTO
	 * @return 저장된 캠핑지 정보를 포함하는 응답 DTO
	 */
	@Transactional
	public CampResponse createCamp(CampRequest campRequestDTO) {
		// 주어진 DTO를 기반으로 Camp 엔티티를 생성.
		CampEntity campEntity = CampRequest.toEntity(campRequestDTO);

		// 설명을 생성하여 캠핑지 엔티티에 설정.
		CampDescriptionEntity descriptionEntity = descriptionService.createDescription(campRequestDTO.getDescription());
		campEntity.setDescription(descriptionEntity);

		// 이미지 목록을 생성하여 캠핑지 엔티티에 추가.
		List<CampImageEntity> imageEntities = imageService.createImages(campRequestDTO.getImageUrls());
		campEntity.addImages(imageEntities);

		// 카테고리 목록을 생성하거나 조회하여 캠핑지 엔티티에 추가.
		List<CategoryEntity> categoryEntities = categoryService.findOrCreateCategories(campRequestDTO.getCategories());
		campEntity.addCategories(categoryEntities);

		// 캠핑지 엔티티를 데이터베이스에 저장.
		CampEntity savedCamp = campRepository.save(campEntity);

		// 저장된 Camp 엔티티를 응답 DTO로 변환하여 반환.
		return CampResponse.fromEntity(savedCamp);
	}
}