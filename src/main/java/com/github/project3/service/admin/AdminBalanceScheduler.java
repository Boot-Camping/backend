package com.github.project3.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBalanceScheduler {

    private final AdminService adminService;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateAdminBalance(){
        adminService.updateAdminBalance();
    }
}
