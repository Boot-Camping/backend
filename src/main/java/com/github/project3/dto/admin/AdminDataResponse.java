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

    private Integer lastDayAdminSales;
    private Integer lastWeekAdminSales;
    private Integer lastMonthAdminSales;
    private Integer totalAdminSales;

    public static AdminDataResponse from(
            long lastDayUserCount, long lastWeekUserCount, long lastMonthUserCount, long totalUserCount,
            long lastDayBookCount, long lastWeekBookCount, long lastMonthBookCount, long totalBookCount,
            long lastDayAdminSales, long lastWeekAdminSales, long lastMonthAdminSales, long totalAdminSales
    ) {
        return new AdminDataResponse(
                (int) lastDayUserCount, (int) lastWeekUserCount, (int) lastMonthUserCount, (int) totalUserCount,
                (int) lastDayBookCount, (int) lastWeekBookCount, (int) lastMonthBookCount, (int) totalBookCount,
                (int) lastDayAdminSales, (int) lastWeekAdminSales, (int) lastMonthAdminSales, (int) totalAdminSales
        );
    }
}
