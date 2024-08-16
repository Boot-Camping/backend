package com.github.project3.repository.camp;

import com.github.project3.entity.camp.CampDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampDescriptionRepository extends JpaRepository<CampDescriptionEntity, Integer> {

}
