package com.github.project3.repository.bookDate;

import com.github.project3.entity.book.BookDateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDateRepository extends JpaRepository<BookDateEntity, Integer> {
}
