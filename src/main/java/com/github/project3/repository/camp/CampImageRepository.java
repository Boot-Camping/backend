package com.github.project3.repository.camp;

import com.github.project3.entity.camp.CampImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampImageRepository extends JpaRepository<CampImageEntity, Integer> {
	// 추가적인 커스텀 쿼리가 필요하면 여기서 정의합니다.
}
