package com.github.project3.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBalanceScheduler {

    private final AdminNoticeService adminNoticeService;

    @Scheduled(cron = "0 0 * * * *")
    public void updateAdminBalance(){
        adminNoticeService.updateAdminBalance();
    }
}
