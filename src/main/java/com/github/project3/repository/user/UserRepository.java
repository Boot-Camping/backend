package com.github.project3.repository.user;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.repository.admin.CreatedAtRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, CreatedAtRepository {

    Optional<UserEntity> findByLoginId(String loginId);

    Optional<UserEntity> findByRole(Role role);
    boolean existsByEmail(String email);

    boolean existsByLoginId(String loginId);
    List<UserEntity> findAllByOrderByCreatedAtDesc();
}
