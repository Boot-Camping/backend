package com.github.project3.service.camp;

import com.github.project3.entity.camp.CategoryEntity;
import com.github.project3.repository.camp.CategoryRepository;
import com.github.project3.service.exceptions.CategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * 주어진 카테고리 이름 목록을 바탕으로, 해당 이름의 카테고리를 조회하거나 없으면 새로 생성하여 반환.
	 *
	 * @param categoryNames 카테고리 이름 목록
	 * @return 주어진 이름에 해당하는 CategoryEntity 객체 리스트
	 */
	public List<CategoryEntity> findOrCreateCategories(List<String> categoryNames) {
		try {
			// categoryNames 리스트를 스트림으로 변환하여 각 이름에 대해 처리.
			return categoryNames.stream()
					.map(categoryName ->
							// 카테고리 이름으로 데이터베이스에서 해당 엔티티를 찾음.
							categoryRepository.findByName(categoryName)
									// 만약 해당 이름의 카테고리가 존재하지 않으면, 새로운 카테고리를 생성하여 저장.
									.orElseGet(() -> categoryRepository.save(
											CategoryEntity.builder().name(categoryName).build())
									)
					)
					// 최종적으로 처리된 CategoryEntity 객체들을 리스트로 수집.
					.collect(Collectors.toList()); // Collectors.toList()로 변경
		} catch (Exception e) {
			// 카테고리 처리 중 예외가 발생하면 CategoryException을 발생시킴.
			throw new CategoryException("카테고리 처리 중 오류가 발생했습니다.", e);
		}
	}
}