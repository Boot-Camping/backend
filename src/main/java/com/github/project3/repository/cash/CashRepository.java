package com.github.project3.repository.cash;

import com.github.project3.entity.user.CashEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashRepository extends JpaRepository<CashEntity, Integer> {

}
