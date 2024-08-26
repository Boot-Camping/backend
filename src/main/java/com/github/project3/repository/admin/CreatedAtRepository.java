package com.github.project3.repository.admin;

import com.github.project3.entity.user.CashEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CreatedAtRepository {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
