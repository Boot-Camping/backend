package com.github.project3.repository.book;

import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {

    boolean existsByCampAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            CampEntity camp, Status status, LocalDateTime checkOut, LocalDateTime checkIn);
}
