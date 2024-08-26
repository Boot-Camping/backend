package com.github.project3.controller.camp;

import com.github.project3.dto.camp.*;
import com.github.project3.service.camp.CampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/camp")
@RequiredArgsConstructor
@Tag(name = "Camp API", description = "캠핑지 관련 API")
public class CampController {

	private final CampService campService;

	/**
	 * 새로운 캠핑지를 등록.
	 *
	 * @param request 캠핑지 등록 요청 데이터 (multipart/form-data 형식)
	 * @return 등록된 캠핑지에 대한 응답 데이터
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "캠핑지 등록", description = "새로운 캠핑지를 등록합니다.")
	public ResponseEntity<CampResponse> createCamp(@ModelAttribute CampRequest request) {
		CampResponse response = campService.createCamp(request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 캠핑지 전체 조회 (페이지네이션 적용)
	 *
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@GetMapping
	@Operation(
			summary = "캠핑지 전체 조회",
			description = "등록된 모든 캠핑지 정보를 페이지네이션으로 조회합니다."
	)
	public ResponseEntity<CampPageResponse> getAllCamps(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		CampPageResponse response = campService.getAllCamps(page, size);
		return ResponseEntity.ok(response);
	}

	/**
	 * 캠핑지 정보 수정
	 *
	 * @param updateRequest 캠핑지 수정 요청 데이터 (multipart/form-data 형식)
	 * @return 수정된 캠핑지에 대한 응답 데이터
	 */
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "캠핑지 수정", description = "기존 캠핑지 정보를 수정합니다.")
	public ResponseEntity<CampResponse> updateCamp(@ModelAttribute CampUpdateRequest updateRequest) {
		CampResponse response = campService.updateCamp(updateRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * 캠핑지 삭제
	 *
	 * @param campId 삭제할 캠핑지의 ID
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/{campId}")
	@Operation(summary = "캠핑지 삭제", description = "등록된 캠핑지를 삭제합니다.")
	public ResponseEntity<String> deleteCamp(@PathVariable Integer campId) {
		campService.deleteCamp(campId);
		return ResponseEntity.ok("캠핑지가 성공적으로 삭제되었습니다.");
	}

	/**
	 * 특정 캠핑지의 세부 정보를 조회합니다.
	 *
	 * @param campId 조회할 캠핑지의 ID
	 * @return 조회된 캠핑지의 세부 정보와 예약된 날짜들이 포함된 응답 객체
	 */
	@GetMapping("/{campId}")
	@Operation(summary = "캠핑지 세부 정보 조회", description = "특정 캠핑지의 세부 정보를 조회합니다.")
	public ResponseEntity<CampSpecResponse> getCampById(@PathVariable Integer campId) {
		// 캠핑지 ID를 사용하여 캠핑지의 세부 정보를 조회
		CampSpecResponse response = campService.getCampById(campId);
		return ResponseEntity.ok(response);
	}

	/**
	 * 카테고리별로 캠핑지를 조회합니다. 페이지네이션이 적용됩니다.
	 *
	 * @param categoryName 조회할 카테고리 이름
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@GetMapping("/category")
	@Operation(summary = "카테고리별 캠핑지 조회", description = "카테고리별로 캠핑지를 조회합니다. 페이지네이션이 적용됩니다.")
	public ResponseEntity<CampPageResponse> getCampsByCategory(
			@RequestParam String categoryName,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		CampPageResponse response = campService.getCampsByCategory(categoryName, page, size);
		return ResponseEntity.ok(response);
	}

	/**
	 * 주소 기반으로 캠핑지를 검색합니다. 페이지네이션이 적용됩니다.
	 *
	 * @param addr 검색할 주소 이름
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@GetMapping("/addr")
	@Operation(summary = "주소 기반 캠핑지 검색", description = "주소를 기준으로 캠핑지를 검색합니다. 페이지네이션이 적용됩니다.")
	public ResponseEntity<CampPageResponse> getCampsByAddr(
			@RequestParam String addr,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		CampPageResponse response = campService.getCampsByAddr(addr, page, size);
		return ResponseEntity.ok(response);
	}

	/**
	 * 주소 기반으로 캠핑지를 검색합니다. 페이지네이션이 적용됩니다.
	 *
	 * @param campName 검색할 캠핑지 이름
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @return 페이지네이션이 적용된 캠핑지 응답 리스트
	 */
	@GetMapping("/campName")
	@Operation(summary = "캠핑지 이름 기반 검색",description = "캠핑지 이름을 기준으로 캠핑지를 검색합니다. 페이지네이션 적용")
	public ResponseEntity<CampPageResponse> getCampsByCampName(
			@RequestParam String campName,
			@RequestParam (defaultValue = "0") int page,
			@RequestParam (defaultValue = "10") int size) {

		CampPageResponse response = campService.getCampsByNmae(campName, page, size);
		return ResponseEntity.ok(response);

	}


}
