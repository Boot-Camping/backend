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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
	 * @param request 캠핑지 등록 요청 데이터
	 * @return 등록된 캠핑지에 대한 응답 데이터
	 */
	@PostMapping
	@Operation(summary = "캠핑지 등록", description = "새로운 캠핑지를 등록합니다.")
	public ResponseEntity<CampResponse> createCamp(@RequestBody CampRequest request) {
		CampResponse response = campService.createCamp(request);
		return ResponseEntity.ok(response);
	}

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
	 * @param updateRequest 캠핑지 수정 요청 데이터
	 * @return 수정된 캠핑지에 대한 응답 데이터
	 */
	@PutMapping
	@Operation(summary = "캠핑지 수정", description = "기존 캠핑지 정보를 수정합니다.")
	public ResponseEntity<CampResponse> updateCamp(@RequestBody CampUpdateRequest updateRequest) {
		CampResponse response = campService.updateCamp(updateRequest);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{campId}")
	@Operation(summary = "캠핑지 삭제", description = "등록된 캠핑지를 삭제합니다.")
	public ResponseEntity<String> deleteCamp(@PathVariable Integer campId) {
		campService.deleteCamp(campId);
		return ResponseEntity.ok("캠핑지가 성공적으로 삭제되었습니다.");
	}
}
