package com.github.project3.repository.mypage;

import com.github.project3.entity.user.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileImageRepository extends JpaRepository<UserImageEntity, Integer> {
}
