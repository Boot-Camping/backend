package com.github.project3.repository.book;

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
import java.util.Optional;

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

    List<BookEntity> findByUserId(Integer userId);

    //조건에 해당하는 예약 상태를 변경
    @Modifying
    @Transactional
    @Query("UPDATE BookEntity b SET b.status = 'DECIDE' WHERE b.endDate <= CURRENT_TIMESTAMP AND b.status = 'BOOKING'")
    void updateStatusIfEndDatePassed();

    List<BookEntity> findAllByStartDateBeforeAndStatus(LocalDateTime StartDate, Status status);

    @Query("SELECT SUM(b.totalPrice) FROM BookEntity b WHERE b.createdAt BETWEEN :start AND :end AND b.status = 'DECIDE' ")
    long sumTotalPriceByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(b.totalPrice) FROM BookEntity b WHERE b.status = 'DECIDE' ")
    long sumTotalPrice();
}
