package com.github.project3.repository.book;

import com.github.project3.dto.book.BookInquiryResponse;
import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.repository.admin.CreatedAtRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer>, CreatedAtRepository {

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM BookEntity b " +
            "WHERE b.camp = :camp " +
            "AND b.status IN :statuses " +
            "AND ((b.startDate <= :checkOut AND b.endDate >= :checkIn) " +
            "OR (b.startDate >= :checkIn AND b.startDate <= :checkOut))")
    boolean existsByCampAndDateRangeOverlap(@Param("camp") CampEntity camp,
                                            @Param("checkIn") LocalDateTime checkIn,
                                            @Param("checkOut") LocalDateTime checkOut,
                                            @Param("statuses") List<Status> statuses);

    // JPQL 을 사용해 조회 결과를 DTO 에 직접 매핑
    @Query("SELECT DISTINCT new com.github.project3.dto.book.BookInquiryResponse(" +
            "b.id, c.id, c.name, " +
            "(SELECT CASE WHEN COUNT(ci) > 0 THEN ci.imageUrl ELSE NULL END FROM CampImageEntity ci WHERE ci.camp = c), " +
            "b.startDate, b.endDate, b.num, b.totalPrice, b.request, b.status) " +
            "FROM BookEntity b " +
            "JOIN b.camp c " +
            "LEFT JOIN c.images ci " +
            "WHERE b.user.id = :userId")
    List<BookInquiryResponse> findBookInquiriesByUserId(@Param("userId") Integer userId);

    //조건에 해당하는 예약 상태를 변경
    @Modifying
    @Transactional
    @Query("UPDATE BookEntity b SET b.status = 'DECIDE' WHERE b.endDate <= CURRENT_TIMESTAMP AND b.status = 'BOOKING'")
    void updateStatusIfEndDatePassed();

    List<BookEntity> findAllByStartDateBeforeAndStatus(LocalDateTime StartDate, Status status);

    @Query("SELECT COALESCE(SUM(b.totalPrice), 0) FROM BookEntity b WHERE b.startDate BETWEEN :start AND :end AND b.status = 'DECIDE' ")
    Long sumTotalPriceByStartDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(b.totalPrice) FROM BookEntity b WHERE b.status = 'DECIDE' ")
    long sumTotalPrice();

    // 특정 사용자와 캠핑장에 대한 구매 확정 상태의 예약을 찾는 메서드 추가
    List<BookEntity> findByUserIdAndCampIdAndStatus(Integer userId, Integer campId, Status status);
}
