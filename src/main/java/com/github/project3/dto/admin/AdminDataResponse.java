package com.github.project3.dto.admin;

import com.github.project3.entity.book.BookEntity;
import com.github.project3.entity.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDataResponse {
    private Integer lastDayUserCount;
    private Integer lastWeekUserCount;
    private Integer lastMonthUserCount;
    private Integer totalUserCount;

    private Integer lastDayBookCount;
    private Integer lastWeekBookCount;
    private Integer lastMonthBookCount;
    private Integer totalBookCount;

    private Integer lastDayAdminBalance;
    private Integer lastWeekAdminBalance;
    private Integer lastMonthAdminBalance;
    private Integer totalAdminBalance;

    public static AdminDataResponse from(
            long lastDayUserCount, long lastWeekUserCount, long lastMonthUserCount, long totalUserCount,
            long lastDayBookCount, long lastWeekBookCount, long lastMonthBookCount, long totalBookCount,
            long lastDayAdminBalance, long lastWeekAdminBalance, long lastMonthAdminBalance, long totalAdminBalance
    ) {
        return new AdminDataResponse(
                (int) lastDayUserCount, (int) lastWeekUserCount, (int) lastMonthUserCount, (int) totalUserCount,
                (int) lastDayBookCount, (int) lastWeekBookCount, (int) lastMonthBookCount, (int) totalBookCount,
                (int) lastDayAdminBalance, (int) lastWeekAdminBalance, (int) lastMonthAdminBalance, (int) totalAdminBalance
        );
    }
}
