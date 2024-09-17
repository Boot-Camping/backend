package com.github.project3.repository.admin;

import com.github.project3.entity.admin.AdminEntity;
import com.github.project3.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Integer> {
    Optional<AdminEntity> findByUser(UserEntity user);

    @Query("Select sales From AdminEntity")
    Long findSales();
}
