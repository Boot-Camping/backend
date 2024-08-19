package com.github.project3.repository.bookDate;

import com.github.project3.entity.book.BookDateEntity;
import com.github.project3.entity.book.enums.Status;
import com.github.project3.entity.camp.CampEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookDateRepository extends JpaRepository<BookDateEntity, Integer> {
	@Query("SELECT bd.date FROM BookDateEntity bd WHERE bd.book.camp.id = :campId")
	List<LocalDateTime> findReservedDatesByCampId(@Param("campId") Integer campId);

}
