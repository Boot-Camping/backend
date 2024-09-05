# 📚 캠핑 예약 시스템 백엔드

### 프로젝트 개요
Spring Boot 기반의 **캠핑 예약 시스템** 백엔드입니다. 사용자는 캠핑장 예약, 리뷰 작성, 찜 목록 관리 등 다양한 기능을 사용할 수 있습니다. JWT를 활용한 인증, AWS S3를 통한 파일 관리 기능도 포함되어 있습니다.

---

### 주요 기능

- **유저 관리**: 회원가입, 로그인, 비밀번호 수정, 프로필 이미지 업데이트
- **캠핑장 관리**: 캠핑장 등록, 수정, 삭제, 카테고리별 검색
- **예약 관리**: 캠핑장 예약 생성, 조회, 취소
- **리뷰 및 댓글**: 리뷰 작성, 댓글 기능
- **마이페이지**: 찜 목록, 예약 내역 관리
- **파일 업로드**: AWS S3를 이용한 이미지 저장
- **실시간 채팅**: WebSocket 기반의 채팅 기능
- **예약 상태 스케줄러**: 예약 상태 자동 업데이트

---

### 기술 스택

- **Backend**: Spring Boot, Spring Security, JPA
- **Database**: MySQL/MariaDB, H2
- **Authentication**: JWT (JSON Web Token)
- **File Storage**: AWS S3
- **API 문서화**: Swagger, SpringDoc
- **실시간 기능**: WebSocket
- **빌드 도구**: Gradle

---

### API Reference

- Swagger 참고 : http://localhost:8080/swagger-ui.html
