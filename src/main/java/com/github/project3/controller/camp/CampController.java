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
@RequestMapping("/api/camps")
@RequiredArgsConstructor
@Tag(name = "Camp API", description = "캠핑지 관련 API")
public class CampController {

	private final CampService campService;

	/**
	 * 새로운 캠핑지를 등록합니다.
	 *
	 * @param request 클라이언트로부터 받은 캠핑지 등록 요청 데이터 (multipart/form-data 형식)
	 * @return 등록된 캠핑지에 대한 응답 데이터를 포함한 ResponseEntity 객체
	 */
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "캠핑지 등록", description = "새로운 캠핑지를 등록합니다.")
	public ResponseEntity<CampResponse> createCamp(@ModelAttribute CampRequest request) {
		CampResponse response = campService.createCamp(request);
		return ResponseEntity.ok(response);
	}

	/**
	 * 캠핑지 검색 및 전체 조회를 처리합니다.
	 *
	 * - 카테고리, 주소, 이름을 기준으로 검색할 수 있습니다.
	 * - 검색 조건이 없을 경우 전체 캠핑지를 페이지네이션으로 조회합니다.
	 *
	 * @param categoryName 검색할 카테고리 이름 (선택적)
	 * @param addr 검색할 주소 (선택적)
	 * @param name 검색할 캠핑지 이름 (선택적)
	 * @param page 페이지 번호 (기본값 0)
	 * @param size 페이지 크기 (기본값 10)
	 * @return 검색된 캠핑지 정보를 페이지네이션이 적용된 CampPageResponse 객체로 반환
	 */
	@GetMapping
	@Operation(summary = "캠핑지 검색 및 전체 조회", description = "등록된 캠핑지 정보를 검색하거나, 페이지네이션으로 전체 조회합니다.")
	public ResponseEntity<CampPageResponse> searchCamps(
			@RequestParam(required = false) String categoryName,
			@RequestParam(required = false) String addr,
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		CampPageResponse response = campService.searchCamps(categoryName, addr, name, page, size);
		return ResponseEntity.ok(response);
	}

	/**
	 * 기존 캠핑지의 정보를 수정합니다.
	 *
	 * @param updateRequest 클라이언트로부터 받은 캠핑지 수정 요청 데이터 (multipart/form-data 형식)
	 * @return 수정된 캠핑지 정보를 포함한 ResponseEntity 객체
	 */
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "캠핑지 수정", description = "기존 캠핑지 정보를 수정합니다.")
	public ResponseEntity<CampResponse> updateCamp(@ModelAttribute CampUpdateRequest updateRequest) {
		CampResponse response = campService.updateCamp(updateRequest);
		return ResponseEntity.ok(response);
	}

	/**
	 * 특정 캠핑지를 삭제합니다.
	 *
	 * @param campId 삭제할 캠핑지의 ID
	 * @return 삭제 성공 메시지를 포함한 ResponseEntity 객체
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
	 * @return 조회된 캠핑지의 세부 정보와 예약된 날짜들이 포함된 CampSpecResponse 객체
	 */
	@GetMapping("/{campId}")
	@Operation(summary = "캠핑지 세부 정보 조회", description = "특정 캠핑지의 세부 정보를 조회합니다.")
	public ResponseEntity<CampSpecResponse> getCampById(@PathVariable Integer campId) {
		CampSpecResponse response = campService.getCampById(campId);
		return ResponseEntity.ok(response);
	}
}
