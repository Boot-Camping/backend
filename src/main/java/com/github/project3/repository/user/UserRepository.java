package com.github.project3.repository.user;

import com.github.project3.dto.admin.AdminUserCheckResponse;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.repository.admin.CreatedAtRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, CreatedAtRepository {

    Optional<UserEntity> findByLoginId(String loginId);

    Optional<UserEntity> findByRole(Role role);
    boolean existsByEmail(String email);

    boolean existsByLoginId(String loginId);

    @Query("SELECT new com.github.project3.dto.admin.AdminUserCheckResponse(u.id, u.loginId, u.name, u.email, u.tel, u.status)" +
            "FROM UserEntity u ORDER BY u.createdAt DESC")
    List<AdminUserCheckResponse> findAllUsersWithDetails();
}
