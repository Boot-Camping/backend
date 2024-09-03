package com.github.project3.repository.mypage;

import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.wishlist.WishlistEntity;
import com.github.project3.entity.wishlist.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, Integer> {
    boolean existsByCampAndUser(CampEntity camp, UserEntity user);
    List<WishlistEntity> findByUserAndStatus(UserEntity user, Status status);
    WishlistEntity findByCampAndUser(CampEntity camp, UserEntity user);
}
