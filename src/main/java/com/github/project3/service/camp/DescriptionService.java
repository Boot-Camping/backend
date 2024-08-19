package com.github.project3.service.camp;

import com.github.project3.dto.camp.CampDescriptionDTO;
import com.github.project3.entity.camp.CampDescriptionEntity;
import com.github.project3.service.exceptions.DescriptionException;
import org.springframework.stereotype.Service;

@Service
public class DescriptionService {

	/**
	 * 주어진 설명 문자열을 기반으로 CampDescriptionEntity 객체를 생성.
	 *
	 * @param description 캠핑지 설명 문자열
	 * @return 생성된 CampDescriptionEntity 객체
	 */
	public CampDescriptionEntity createDescription(String description) {
		try {
			// 설명 문자열을 DTO로 변환한 후, 이를 엔티티로 변환하여 반환.
			return CampDescriptionDTO.toEntity(
					// CampDescriptionDTO 객체를 빌더 패턴을 통해 생성.
					CampDescriptionDTO.builder().description(description).build()
			);
		} catch (Exception e) {
			// 설명 생성 중 예외가 발생하면 DescriptionException을 발생시킴.
			throw new DescriptionException("설명 생성 중 오류가 발생했습니다.", e);
		}
	}
}
