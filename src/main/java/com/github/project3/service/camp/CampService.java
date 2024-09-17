package com.github.project3.service.camp;

import com.github.project3.dto.camp.*;
import com.github.project3.entity.camp.*;

import com.github.project3.repository.bookDate.BookDateRepository;
import com.github.project3.repository.camp.CampRepository;
import com.github.project3.repository.camp.ViewCountRepository;
import com.github.project3.repository.review.ReviewRepository;
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
	private final ViewCountRepository viewCountRepository;
	private final ReviewRepository reviewRepository;
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
	 * 검색 조건에 따라 캠핑지를 검색하고, 페이지네이션을 적용하여 결과를 반환합니다.
	 *
	 * 이 메서드는 카테고리 이름, 주소, 캠핑지 이름을 기반으로 검색할 수 있습니다.
	 * 검색 조건이 없으면 전체 캠핑지를 검색합니다.
	 * 각 캠핑지에 대해 평균 평점, 리뷰 수, 예약된 날짜 수를 함께 계산하여 반환합니다.
	 *
	 * @param categoryName 검색할 카테고리 이름 (선택적) - 특정 카테고리에 속한 캠핑지를 검색합니다.
	 * @param addr 검색할 주소 (선택적) - 주소에 해당하는 캠핑지를 검색합니다. 주소는 부분 일치를 사용합니다.
	 * @param name 검색할 캠핑지 이름 (선택적) - 이름에 해당하는 캠핑지를 검색합니다. 이름은 부분 일치를 사용합니다.
	 * @param page 페이지 번호 (기본값 0) - 반환할 페이지의 번호를 지정합니다.
	 * @param size 페이지 크기 (기본값 10) - 한 페이지에 포함될 항목 수를 지정합니다.
	 * @return 검색된 캠핑지 정보를 포함한 페이지네이션된 CampPageResponse 객체
	 *
	 * @throws NotFoundException 검색 조건에 맞는 캠핑지가 없는 경우 발생합니다.
	 *
	 * @apiNote 이 메서드는 다양한 검색 조건에 따라 캠핑지를 조회하며,
	 *          각 캠핑지에 대해 평균 평점, 리뷰 수, 예약된 날짜 수를 함께 반환합니다.
	 *          검색 조건이 없으면 전체 캠핑지를 조회하며, 페이지네이션을 적용합니다.
	 */
	@Transactional
	public CampPageResponse searchCamps(String categoryName, String addr, String name, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CampDataDTO> campDataDTOPage;

		// 카테고리 이름이 주어진 경우, 해당 카테고리에 속한 캠핑지를 검색합니다.
		if (categoryName != null) {
			campDataDTOPage = campRepository.findCampsWithStatisticsByCategoryName(categoryName, pageable);
		}
		// 주소가 주어진 경우, 해당 주소에 포함된 캠핑지를 검색합니다.
		else if (addr != null) {
			campDataDTOPage = campRepository.findCampsWithStatisticsByAddr(addr, pageable);
		}
		// 이름이 주어진 경우, 해당 이름을 포함하는 캠핑지를 검색합니다.
		else if (name != null) {
			campDataDTOPage = campRepository.findCampsWithStatisticsByName(name, pageable);
		}
		// 검색 조건이 없으면 전체 캠핑지를 검색합니다.
		else {
			campDataDTOPage = campRepository.findCampsWithStatistics(pageable);
		}

		// 검색된 캠핑지가 없으면 NotFoundException을 발생시킵니다.
		if (campDataDTOPage.isEmpty()) {
			throw new NotFoundException("검색 조건에 맞는 캠핑지가 없습니다.");
		}

		// 검색된 캠핑지들을 CampResponse로 변환하여 페이지네이션된 응답 객체를 생성합니다.
		Page<CampResponse> campResponses = campDataDTOPage.map(campDataDTO ->
				CampResponse.fromEntity(campDataDTO.getCampEntity(),
						campDataDTO.getAverageGrade(),
						campDataDTO.getReviewCount(),
						campDataDTO.getReservedDateCount())
		);

		// CampPageResponse 객체로 변환하여 반환합니다.
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
	 *
	 * @param campId 삭제할 캠핑지의 ID
	 */
	@Transactional
	public void deleteCamp(Integer campId) {
		campRepository.findById(campId)
				.orElseThrow(() -> new NotFoundException("해당 캠핑지를 찾을 수 없습니다."));


		campRepository.deleteById(campId); // 여기서 DataIntegrityViolationException 발생 가능
	}

	/**
	 * 특정 캠핑지의 세부 정보를 조회합니다.
	 *
	 * @param campId 조회할 캠핑지의 ID
	 * @return 조회된 캠핑지의 세부 정보와 예약된 날짜들이 포함된 CampSpecResponse 객체
	 */
	@Transactional
	public CampSpecResponse getCampById(Integer campId) {
		// 주어진 캠핑지 ID로 캠핑지 정보를 조회, 존재하지 않을 경우 NotFoundException을 발생
		CampEntity campEntity = campRepository.findById(campId)
				.orElseThrow(() -> new NotFoundException("해당 캠핑지를 찾을 수 없습니다."));

		// 조회수 증가 처리
		ViewCountEntity viewCountEntity = viewCountRepository.findByCampId(campId)
				.orElseGet(() -> ViewCountEntity.createWithInitialCount(campEntity));

		viewCountEntity.incrementCount(); // 조회수 증가 메서드
		viewCountRepository.save(viewCountEntity);

		// 해당 캠핑지의 예약된 날짜들을 조회
		List<LocalDateTime> reservedDates = bookDateRepository.findReservedDatesByCampId(campId);

		// 해당 캠핑지의 조회수 가져오기
		Integer viewCount = viewCountRepository.findByCampId(campId)
				.map(ViewCountEntity::getCount)
				.orElse(0);

		// 해당 캠핑지의 평점 계산
		Double averageGrade = reviewRepository.calculateAverageGradeByCampId(campId);


		// CampSpecResponse의 스태틱 팩토리 메서드를 사용하여 캠핑지 엔티티와 예약된 날짜들을 응답 객체로 변환하여 반환
		return CampSpecResponse.fromEntity(campEntity, reservedDates, viewCount, averageGrade);
	}
}