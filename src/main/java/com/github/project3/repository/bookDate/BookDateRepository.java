package com.github.project3.repository.bookDate;

import com.github.project3.entity.book.BookDateEntity;
import com.github.project3.entity.camp.CampEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BookDateRepository extends JpaRepository<BookDateEntity, Integer> {
    boolean existsByBook_CampAndDateBetween(CampEntity camp, LocalDateTime startDate, LocalDateTime endDate);
}
