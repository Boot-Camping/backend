package com.github.project3.repository.review;

import com.github.project3.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
    // 특정 캠핑장에 대한 리뷰 목록을 가져오는 메서드
    List<ReviewEntity> findByCampId(Integer campId);
    // 특정 사용자가 작성한 리뷰 목록을 가져오는 메서드
    List<ReviewEntity> findByUserId(Integer userId);

    // 특정 캠핑장에 대한 리뷰 개수를 세는 메서드
    long countByCampId(Integer campId);
}
