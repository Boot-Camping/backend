package com.github.project3.service.camp;

import com.github.project3.dto.camp.*;
import com.github.project3.entity.camp.*;

import com.github.project3.repository.bookDate.BookDateRepository;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.FileUploadException;
import com.github.project3.service.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampService {

	private final CampRepository campRepository;
	private final BookDateRepository bookDateRepository;
	private final DescriptionService descriptionService;
	private final ImageService imageService;
	private final CategoryService categoryService;
	private final S3Service s3Service;

	/**
	 * 캠핑지 등록 요청을 처리.
	 * 주어진 DTO 데이터를 바탕으로 캠핑지 엔티티를 생성한 후 데이터베이스에 저장.
	 *
	 * @param campRequest 클라이언트로부터 받은 캠핑지 등록 요청 DTO
	 * @return 저장된 캠핑지 정보를 포함하는 응답 DTO
	 */
	@Transactional
	public CampResponse createCamp(CampRequest campRequest) {
		// 주어진 DTO를 기반으로 Camp 엔티티를 생성.
		CampEntity campEntity = CampRequest.toEntity(campRequest);

		// 설명을 생성하여 캠핑지 엔티티에 설정.
		CampDescriptionEntity descriptionEntity = descriptionService.createDescription(campRequest.getDescription());
		campEntity.setDescription(descriptionEntity);

		// 이미지 업로드 처리
		List<String> imageUrls = campRequest.getImageFiles().stream()
				.map(file -> {
					try {
						return s3Service.uploadCampImage(file);
					} catch (IOException e) {
						// 파일 업로드 중 문제가 발생하면 FileUploadException을 발생.
						throw new FileUploadException("이미지 업로드 중 오류가 발생했습니다.", e);
					}
				})
				.collect(Collectors.toList());

		// 이미지 목록을 생성하여 캠핑지 엔티티에 추가.
		List<CampImageEntity> imageEntities = imageService.createImages(imageUrls, campEntity);
		campEntity.addImages(imageEntities);

		// 카테고리 목록을 생성하거나 조회하여 캠핑지 엔티티에 추가.
		List<CategoryEntity> categoryEntities = categoryService.findOrCreateCategories(campRequest.getCategories());
		campEntity.addCategories(categoryEntities);

		// 캠핑지 엔티티를 데이터베이스에 저장.
		CampEntity savedCamp = campRepository.save(campEntity);

		// 저장된 Camp 엔티티를 응답 DTO로 변환하여 반환.
		return CampResponse.fromEntity(savedCamp);
	}

	/**
	 * 모든 캠핑지 조회 (페이지네이션 적용)
	 *
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@Transactional
	public CampPageResponse getAllCamps(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CampEntity> campEntities = campRepository.findAll(pageable);

		// CampEntity를 CampResponse로 변환하여 Page로 반환
		Page<CampResponse> campResponses = campEntities.map(CampResponse::fromEntity);

		// CampPageResponse DTO로 변환하여 반환
		return new CampPageResponse(campResponses);
	}

	/**
	 * 캠핑지 정보 수정
	 *
	 * @param updateRequest 캠핑지 수정 요청 데이터
	 * @return 수정된 캠핑지 응답 데이터
	 */
	@Transactional
	public CampResponse updateCamp(CampUpdateRequest updateRequest) {
		// 기존 캠핑지 조회
		CampEntity campEntity = campRepository.findById(updateRequest.getId())
				.orElseThrow(() -> new RuntimeException("Camp not found"));

		// 기존 값 업데이트
		campEntity.setName(updateRequest.getName());
		campEntity.setPrice(updateRequest.getPrice());
		campEntity.setAddr(updateRequest.getAddr());
		campEntity.setMaxNum(updateRequest.getMaxNum());
		campEntity.setStandardNum(updateRequest.getStandardNum());
		campEntity.setOverCharge(updateRequest.getOverCharge());

		// 설명 업데이트
		CampDescriptionEntity descriptionEntity = descriptionService.createDescription(updateRequest.getDescription());
		campEntity.setDescription(descriptionEntity);

		// 이미지 업데이트: MultipartFile을 통해 S3에 업로드 후 URL 반환
		List<String> imageUrls = updateRequest.getImageFiles().stream()
				.map(file -> {
					try {
						return s3Service.uploadCampImage(file);
					} catch (IOException e) {
						// 파일 업로드 중 문제가 발생하면 FileUploadException을 발생시.
						throw new FileUploadException("이미지 업로드 중 오류가 발생했습니다.", e);
					}
				})
				.collect(Collectors.toList());

		List<CampImageEntity> imageEntities = imageService.createImages(imageUrls, campEntity);
		campEntity.updateImages(imageEntities);

		// 카테고리 업데이트
		List<CategoryEntity> categoryEntities = categoryService.findOrCreateCategories(updateRequest.getCategories());
		campEntity.addCategories(categoryEntities);

		// 캠핑지 저장
		campRepository.save(campEntity);

		// 업데이트된 엔티티를 다시 로드하여 반환
		CampEntity updatedCampEntity = campRepository.findById(updateRequest.getId())
				.orElseThrow(() -> new NotFoundException("업데이트 후 캠핑지를 찾을 수 없습니다."));

		return CampResponse.fromEntity(updatedCampEntity);
	}

	/**
	 * 캠핑지 삭제
	 * @param campId 삭제할 캠핑지의 ID
	 */
	@Transactional
	public void deleteCamp(Integer campId) {
		campRepository.findById(campId)
				.orElseThrow(() -> new NotFoundException("해당 캠핑지를 찾을 수 없습니다."));


		campRepository.deleteById(campId); // 여기서 DataIntegrityViolationException 발생 가능
	}

	@Transactional
	public CampSpecResponse getCampById(Integer campId) {
		// 주어진 캠핑지 ID로 캠핑지 정보를 조회, 존재하지 않을 경우 NotFoundException을 발생
		CampEntity campEntity = campRepository.findById(campId)
				.orElseThrow(() -> new NotFoundException("해당 캠핑지를 찾을 수 없습니다."));

		// 해당 캠핑지의 예약된 날짜들을 조회
		List<LocalDateTime> reservedDates = bookDateRepository.findReservedDatesByCampId(campId);

		// CampSpecResponse의 스태틱 팩토리 메서드를 사용하여 캠핑지 엔티티와 예약된 날짜들을 응답 객체로 변환하여 반환
		return CampSpecResponse.fromEntity(campEntity, reservedDates);
	}

	/**
	 * 카테고리별로 캠핑지를 조회하며 페이지네이션을 적용합니다.
	 *
	 * @param categoryName 카테고리 이름
	 * @param page 페이지 번호
	 * @param size 페이지 크기
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@Transactional
	public CampPageResponse getCampsByCategory(String categoryName, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CampEntity> campEntities = campRepository.findByCategoryName(categoryName, pageable);

		if (campEntities.isEmpty()) {
			// 해당 카테고리에 캠핑지가 없는 경우 예외 처리
			throw new NotFoundException("해당 카테고리에 등록된 캠핑지가 없습니다.");
		}

		// CampEntity를 CampResponse로 변환하여 Page로 반환
		Page<CampResponse> campResponses = campEntities.map(CampResponse::fromEntity);

		// CampPageResponse DTO로 변환하여 반환
		return new CampPageResponse(campResponses);
	}
}