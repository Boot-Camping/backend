package com.github.project3.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeRegisterRequest {
    private String noticeTitle;
    private String noticeDescription;
}
