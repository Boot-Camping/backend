package com.github.project3.service.admin;

import com.github.project3.repository.admin.CreatedAtRepository;
import com.github.project3.repository.book.BookRepository;
import com.github.project3.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CountService {

    public <T extends CreatedAtRepository> long countEntityCreatedBetween(T repository, LocalDateTime start, LocalDateTime end) {
        return repository.countByCreatedAtBetween(start, end);
    }

    public long getLastDayCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return countEntityCreatedBetween(createdAtRepository, oneDayAgo, now);
    }
    public long getLastWeekCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return countEntityCreatedBetween(createdAtRepository, oneWeekAgo, now);
    }
    public long getLastMonthCount(CreatedAtRepository createdAtRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return countEntityCreatedBetween(createdAtRepository, oneMonthAgo, now);
    }
    public long getTotalUserCount(UserRepository userRepository){
        return userRepository.count();
    }
    public long getTotalBookCount(BookRepository bookRepository){
        return bookRepository.count();
    }

}
