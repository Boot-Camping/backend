package com.github.project3.dto.admin;

import com.github.project3.entity.notice.NoticeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminNoticeCheckPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<AdminNoticeCheckResponse> content;
    private int number;
    private int size;
    private int totalPages;
    private long totalElements;

    public static AdminNoticeCheckPageResponse from(Page<NoticeEntity> noticePage){
        List<AdminNoticeCheckResponse> content = noticePage.getContent().stream()
                .map(AdminNoticeCheckResponse::from)
                .collect(Collectors.toList());

        return new AdminNoticeCheckPageResponse(
                content,
                noticePage.getNumber(),
                noticePage.getSize(),
                noticePage.getTotalPages(),
                noticePage.getTotalElements()
        );
    }
}