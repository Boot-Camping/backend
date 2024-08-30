package com.github.project3.service.admin;

import com.github.project3.repository.admin.AdminRepository;
import com.github.project3.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalesService {

    private final AdminRepository adminRepository;

    public SalesService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Long sumTotalPriceByStartDateBetween(BookRepository repository, LocalDateTime start, LocalDateTime end){
        return repository.sumTotalPriceByStartDateBetween(start, end);
    }
    public long getLastDaySales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneDayAgo = now.minusDays(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneDayAgo, now);
    }
    public long getLastWeekSales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneWeekAgo, now);
    }
    public long getLastMonthSales(BookRepository bookRepository){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return sumTotalPriceByStartDateBetween(bookRepository, oneMonthAgo, now);
    }

    public long getTotalSales(AdminRepository adminRepository){
        return adminRepository.findSales();
    }

}
