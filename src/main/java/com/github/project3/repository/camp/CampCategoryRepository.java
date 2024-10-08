package com.github.project3.repository.camp;

import com.github.project3.entity.camp.CampCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampCategoryRepository extends JpaRepository<CampCategoryEntity, Integer> {
}
