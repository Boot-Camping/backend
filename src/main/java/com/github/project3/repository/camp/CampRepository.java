package com.github.project3.repository.camp;

import com.github.project3.entity.camp.CampEntity;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampRepository extends JpaRepository <CampEntity, Integer> {
}
