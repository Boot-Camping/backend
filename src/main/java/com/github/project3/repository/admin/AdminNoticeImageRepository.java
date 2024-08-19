package com.github.project3.repository.admin;

import com.github.project3.entity.notice.NoticeImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminNoticeImageRepository extends JpaRepository<NoticeImageEntity, Integer> {

}
