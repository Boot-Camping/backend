package com.github.project3.service.admin;

import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Role;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBalanceScheduler {

    private final AdminService adminService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateAdminBalance(){
        adminService.updateAdminBalance();
    }
}
