package com.github.project3.repository.camp;


import com.github.project3.entity.camp.ViewCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewCountRepository extends JpaRepository<ViewCountEntity, Integer> {
	Optional<ViewCountEntity> findByCampId(Integer campId);
}
