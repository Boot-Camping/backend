package com.github.project3.repository.camp;

import com.github.project3.entity.camp.CampEntity;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampRepository extends JpaRepository <CampEntity, Integer> {
	List<CampEntity> findAll();

	// 카테고리 이름으로 캠핑지를 조회하며, 페이지네이션을 적용
	@Query("SELECT c FROM CampEntity c JOIN c.campCategories cc WHERE cc.category.name = :categoryName")
	Page<CampEntity> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);
}
