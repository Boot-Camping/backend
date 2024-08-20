package com.github.project3.service.admin;

import com.github.project3.dto.admin.AdminNoticeCheckResponse;
import com.github.project3.dto.admin.AdminNoticeDetailCheckResponse;
import com.github.project3.dto.admin.AdminNoticeRegisterRequest;
import com.github.project3.dto.mypage.NoticeResponse;
import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import com.github.project3.repository.admin.AdminNoticeImageRepository;
import com.github.project3.repository.admin.AdminNoticeRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final AdminNoticeRepository adminNoticeRepository;
    private final AdminNoticeImageRepository adminNoticeImageRepository;
    private final S3Service s3Service;

    // 공지사항 등록
    public NoticeResponse registerNotice(AdminNoticeRegisterRequest registerRequest, List<MultipartFile> images){
        NoticeEntity notice = new NoticeEntity();
        if (registerRequest.getNoticeTitle() == null || registerRequest.getNoticeTitle().isEmpty()){
            throw new IllegalArgumentException("공지 제목을 입력해주세요.");
        } else if (registerRequest.getNoticeDescription() == null || registerRequest.getNoticeDescription().isEmpty()) {
            throw new IllegalArgumentException("공지 내용을 입력해주세요.");
        }
        notice.setTitle(registerRequest.getNoticeTitle());
        notice.setDescription(registerRequest.getNoticeDescription());

        adminNoticeRepository.save(notice);

        if (!images.isEmpty() && images != null){
            for (MultipartFile image : images){
                try {
                    String imageUrl = s3Service.uploadNoticeImage(image);

                    NoticeImageEntity noticeImage = new NoticeImageEntity();
                    noticeImage.setNotice(notice); // 공지사항과 연결
                    noticeImage.setImageUrl(imageUrl);

                    adminNoticeImageRepository.save(noticeImage);
                } catch (IOException e){
                    throw new RuntimeException("이미지 업로드 실패", e);
                }
            }
        }
        return NoticeResponse.from(notice);
    }
    // 공지사항 전체조회
    public List<AdminNoticeCheckResponse> getNoticeAll(Integer page,Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeEntity> noticePage = adminNoticeRepository.findAll(pageable);

        return noticePage.map(AdminNoticeCheckResponse::from).getContent();
    }
    // 공지사항 상세조회
    public AdminNoticeDetailCheckResponse getNoticeDetail(Integer id){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 공지사항을 찾을 수 없습니다."));

        return AdminNoticeDetailCheckResponse.from(notice);
    }

    // 공지사항 수정

    // 공지사항 삭제
    public void removeNotice(Integer id){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 입니다."));

        adminNoticeRepository.delete(notice);
    }
}
