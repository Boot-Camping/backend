package com.github.project3.repository.book;

import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.UserImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    boolean existsByCampAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrStatus(
            CampEntity camp,
            LocalDateTime requestCheckOut,
            LocalDateTime requestCheckIn,
            Status status1,
            Status status2
    );

    List<BookEntity> findByUserId(Integer userId);

    //조건에 해당하는 예약 상태를 변경
    @Modifying
    @Transactional
    @Query("UPDATE BookEntity b SET b.status = 'DECIDE' WHERE b.endDate <= CURRENT_TIMESTAMP AND b.status = 'BOOKING'")
    void updateStatusIfEndDatePassed();
}
