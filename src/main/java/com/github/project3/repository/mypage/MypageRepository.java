package com.github.project3.repository.mypage;

import com.github.project3.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MypageRepository extends JpaRepository<UserEntity, Integer> {

}