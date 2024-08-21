package com.github.project3.repository.admin;

import com.github.project3.entity.notice.NoticeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminNoticeRepository extends JpaRepository<NoticeEntity, Integer> {
    Page<NoticeEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
