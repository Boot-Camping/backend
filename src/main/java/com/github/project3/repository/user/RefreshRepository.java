package com.github.project3.repository.user;

import com.github.project3.entity.user.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<RefreshEntity, Integer> {

    Boolean existsByRefresh(String refreshToken);

    @Transactional
    void deleteByRefresh(String refreshToken);
}

