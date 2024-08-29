package com.github.project3.repository.camp;

import com.github.project3.dto.camp.CampDataDTO;
import com.github.project3.entity.camp.CampEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampRepository extends JpaRepository<CampEntity, Integer> {

	List<CampEntity> findAll();


	@Query("SELECT new com.github.project3.dto.camp.CampDataDTO(c, AVG(r.grade), COUNT(r), COUNT(bd)) " +
			"FROM CampEntity c " +
			"LEFT JOIN c.campCategories cc " + // CampEntity와 CampCategoryEntity의 관계 조인
			"LEFT JOIN cc.category cat " +  // CampCategoryEntity와 CategoryEntity의 관계 조인
			"LEFT JOIN ReviewEntity r ON c.id = r.camp.id " + // CampEntity와 ReviewEntity의 관계 조인
			"LEFT JOIN BookEntity bd ON c.id = bd.camp.id " + // CampEntity와 BookEntity의 관계 조인
			"WHERE cat.name = :categoryName " + // 카테고리 이름으로 필터링
			"GROUP BY c.id")
	Page<CampDataDTO> findCampsWithStatisticsByCategoryName(String categoryName, Pageable pageable);


	@Query("SELECT new com.github.project3.dto.camp.CampDataDTO(c, AVG(r.grade), COUNT(r), COUNT(bd)) " +
			"FROM CampEntity c " +
			"LEFT JOIN ReviewEntity r ON c.id = r.camp.id " + // CampEntity와 ReviewEntity의 관계 조인
			"LEFT JOIN BookEntity bd ON c.id = bd.camp.id " + // CampEntity와 BookEntity의 관계 조인
			"WHERE c.addr LIKE %:addr% " + // 주소로 필터링
			"GROUP BY c.id")
	Page<CampDataDTO> findCampsWithStatisticsByAddr(String addr, Pageable pageable);


	@Query("SELECT new com.github.project3.dto.camp.CampDataDTO(c, AVG(r.grade), COUNT(r), COUNT(bd)) " +
			"FROM CampEntity c " +
			"LEFT JOIN ReviewEntity r ON c.id = r.camp.id " + // CampEntity와 ReviewEntity의 관계 조인
			"LEFT JOIN BookEntity bd ON c.id = bd.camp.id " + // CampEntity와 BookEntity의 관계 조인
			"WHERE c.name LIKE %:name% " + // 캠핑지 이름으로 필터링
			"GROUP BY c.id")
	Page<CampDataDTO> findCampsWithStatisticsByName(String name, Pageable pageable);


	@Query("SELECT new com.github.project3.dto.camp.CampDataDTO(c, AVG(r.grade), COUNT(r), COUNT(bd)) " +
			"FROM CampEntity c " +
			"LEFT JOIN ReviewEntity r ON c.id = r.camp.id " + // CampEntity와 ReviewEntity의 관계 조인
			"LEFT JOIN BookEntity bd ON c.id = bd.camp.id " + // CampEntity와 BookEntity의 관계 조인
			"GROUP BY c.id")
	Page<CampDataDTO> findCampsWithStatistics(Pageable pageable);
}