package com.github.project3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 기존 유저 프로필 이미지 업로드 메서드
    public String uploadUserImage(MultipartFile file) throws IOException {
        return uploadFileToS3(file, "user-profile-images/");
    }

    // 새로운 리뷰 이미지 업로드 메서드
    public String uploadReviewImage(MultipartFile file) throws IOException {
        return uploadFileToS3(file, "reviewImage/");
    }

    // 기존 유저 프로필 이미지 업로드 메서드
    public String uploadNoticeImage(MultipartFile file) throws IOException {
        return uploadFileToS3(file, "admin-notice-images/");
    }

    // 공통된 파일 업로드 메서드
    private String uploadFileToS3(MultipartFile file, String folderName) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = folderName + fileName;

        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), null)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }

        return amazonS3.getUrl(bucketName, filePath).toString();
    }
}
