package com.github.project3.controller.camp;

import com.github.project3.dto.camp.CampRequest;
import com.github.project3.dto.camp.CampResponse;
import com.github.project3.service.camp.CampService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
