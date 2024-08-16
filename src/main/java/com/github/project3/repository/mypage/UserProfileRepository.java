package com.github.project3.repository.mypage;

import com.github.project3.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserEntity, Integer> {
    @Override
    Optional<UserEntity> findById(Integer id);

}
