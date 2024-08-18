package com.github.project3.controller.camp;

import com.github.project3.dto.camp.CampRequest;
import com.github.project3.dto.camp.CampResponse;
import com.github.project3.dto.camp.CampUpdateRequest;
import com.github.project3.service.camp.CampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
	 * 캠핑지 전체 조회
	 *
	 * @return 모든 캠핑지에 대한 응답 데이터 리스트
	 */
	@GetMapping
	@Operation(
			summary = "캠핑지 전체 조회",
			description = "등록된 모든 캠핑지 정보를 조회합니다."
	)
	public ResponseEntity<List<CampResponse>> getAllCamps() {
		List<CampResponse> camps = campService.getAllCamps();
		return ResponseEntity.ok(camps);
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
}
