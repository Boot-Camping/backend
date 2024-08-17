package com.github.project3.repository.mypage;

import com.github.project3.entity.notice.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository <NoticeEntity, Integer> {
    List<NoticeEntity> ();
}
