package com.github.project3.repository.review;

import com.github.project3.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {
    // 태그와 이미지를 가져오기 위한 별도의 쿼리 작성
    @Query("SELECT r FROM ReviewEntity r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.camp c " +
            "WHERE r.camp.id = :campId")
    List<ReviewEntity> findReviewsByCampId(@Param("campId") Integer campId);
    // 유저별 리뷰 조회를 위한 메서드
    @Query("SELECT r FROM ReviewEntity r " +
            "JOIN FETCH r.user u " +
            "JOIN FETCH r.camp c " +
            "WHERE r.user.id = :userId")
    List<ReviewEntity> findReviewsByUserId(@Param("userId") Integer userId);

    // 특정 캠핑장에 대한 리뷰 개수를 세는 메서드
    long countByCampId(Integer campId);

    @Query("SELECT AVG(r.grade) FROM ReviewEntity r WHERE r.camp.id = :campId")
    Double calculateAverageGradeByCampId(@Param("campId") Integer campId);
}
