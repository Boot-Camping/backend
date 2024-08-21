package com.github.project3.service.admin;

import com.github.project3.dto.admin.*;
import com.github.project3.dto.mypage.NoticeResponse;
import com.github.project3.entity.notice.NoticeEntity;
import com.github.project3.entity.notice.NoticeImageEntity;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.entity.user.enums.Status;
import com.github.project3.jwt.JwtTokenProvider;
import com.github.project3.repository.admin.AdminNoticeImageRepository;
import com.github.project3.repository.admin.AdminNoticeRepository;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.S3Service;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final AdminNoticeRepository adminNoticeRepository;
    private final AdminNoticeImageRepository adminNoticeImageRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    // 공지사항 등록
    public NoticeResponse registerNotice(AdminNoticeRegisterRequest registerRequest, List<MultipartFile> images){
        NoticeEntity notice = new NoticeEntity();
        if (registerRequest.getTitle() == null || registerRequest.getTitle().isEmpty()){
            throw new IllegalArgumentException("공지 제목을 입력해주세요.");
        } else if (registerRequest.getDescription() == null || registerRequest.getDescription().isEmpty()) {
            throw new IllegalArgumentException("공지 내용을 입력해주세요.");
        }
        notice.setTitle(registerRequest.getTitle());
        notice.setDescription(registerRequest.getDescription());

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
    public Page<AdminNoticeCheckResponse> getNoticeAll(Integer page,Integer size){
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeEntity> noticePage = adminNoticeRepository.findAllByOrderByCreatedAtDesc(pageable);

        return noticePage.map(AdminNoticeCheckResponse::from);
    }
    // 공지사항 상세조회
    public AdminNoticeDetailCheckResponse getNoticeDetail(Integer id){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 공지사항을 찾을 수 없습니다."));

        return AdminNoticeDetailCheckResponse.from(notice);
    }

    // 공지사항 수정
    public AdminNoticeUpdateResponse getUpdateNotice(Integer id, AdminNoticeUpdateRequest noticeUpdateRequest, List<MultipartFile> images){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 공지사항을 찾을 수 없습니다."));
        if (noticeUpdateRequest.getTitle() == null && noticeUpdateRequest.getDescription() == null){
            throw new NotFoundException("수정사항을 입력해 주세요.");
        }
        if (noticeUpdateRequest.getTitle() == null){
            notice.setDescription(noticeUpdateRequest.getDescription());
        } else if (noticeUpdateRequest.getDescription() == null) {
            notice.setTitle(noticeUpdateRequest.getTitle());
        } else {
            notice.setDescription(noticeUpdateRequest.getDescription());
            notice.setTitle(noticeUpdateRequest.getTitle());
        }

            notice.setDescription(noticeUpdateRequest.getDescription());
            // 기존 이미지 삭제
            List<NoticeImageEntity> removeImages = notice.getImages();
            if (removeImages != null && !removeImages.isEmpty()) {
                adminNoticeImageRepository.deleteAll(removeImages);
                notice.setImages(new ArrayList<>());

            // 이미지 추가
            if (images != null || !images.isEmpty()){
                for (MultipartFile image: images){
                    try {
                        String imageUrl = s3Service.uploadNoticeImage(image);
                        NoticeImageEntity noticeImage = new NoticeImageEntity();
                        noticeImage.setNotice(notice);
                        noticeImage.setImageUrl(imageUrl);

                        adminNoticeImageRepository.save(noticeImage);
                        notice.getImages().add(noticeImage);
                    } catch (IOException e){
                        throw new RuntimeException("이미지 업로드 실패", e);
                    }
                }
            }
            adminNoticeRepository.save(notice);
        }
        return AdminNoticeUpdateResponse.from(notice);
    }

    // 공지사항 삭제
    public void removeNotice(Integer id){
        NoticeEntity notice = adminNoticeRepository.findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 공지사항 입니다."));

        adminNoticeRepository.delete(notice);
    }

    // 회원 블랙리스트 등록
    public void getBlacklist(Integer id){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("해당 유저를 찾을 수 없습니다."));

        if (user.getStatus() == Status.BLACKLIST){
            throw new NotAcceptException("이미 등록된 블랙리스트 입니다.");
        }
        user.setStatus(Status.BLACKLIST);
        userRepository.save(user);
    }

}
