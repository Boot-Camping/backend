//package com.github.project3.service.email;
//
//
//import com.github.project3.dto.email.EmailRequest;
//import com.github.project3.entity.user.UserEntity;
//import com.github.project3.repository.user.UserRepository;
//import com.github.project3.service.exceptions.NotAcceptException;
//import com.github.project3.service.exceptions.NotFoundException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.messaging.MessagingException;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring6.SpringTemplateEngine;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Random;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final UserRepository userRepository;
//    private final JavaMailSender emailSender;
//    private final SpringTemplateEngine templateEngine;
//
//    private String authNum;
//
//    // 랜덤 인증코드 생성
//    public String createCode() {
//        Random random = new Random();
//        StringBuffer key = new StringBuffer();
//
//        for (int i = 0; i < 8; i++) {
//            int index = random.nextInt(4);
//
//            switch (index) {
//                case 0:
//                    key.append((char) ((int) random.nextInt(26) + 97));
//                    break;
//                case 1:
//                    key.append((char) ((int) random.nextInt(26) + 65));
//                    break;
//                default:
//                    key.append(random.nextInt(9));
//            }
//        }
//        return key.toString();
//    }
//
//    //메일 양식 작성
//    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException {
//    try {
//        authNum = createCode(); // 인증 코드 생성 및 할당
//        String setFrom = "khg1070a@gmail.com"; // 보내는 사람 이메일 주소
//        String toEmail = email; // 받는 사람 이메일 주소
//        String title = "CODEBOX 회원가입 인증 번호"; // 이메일 제목
//
//        MimeMessage message = emailSender.createMimeMessage();
//        message.addRecipients(MimeMessage.RecipientType.TO, toEmail); // 받는 사람 이메일 설정
//        message.setSubject(title); // 제목 설정
//        message.setFrom(setFrom); // 보내는 이메일 설정
//        message.setText(setContext(authNum, "emailTemplate"), "utf-8", "html"); // 인증 코드 포함한 템플릿 적용
//
//        return message;
//        }catch (jakarta.mail.MessagingException e) {
//            log.error("메일 작성 중 오류 발생", e);
//            throw e;
//        }
//    }
//
//    //실제 메일 전송
//    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException {
//
//        try {
//            // 메일 전송에 필요한 정보 설정
//            MimeMessage emailForm = createEmailForm(toEmail);
//            // 실제 메일 전송
//            emailSender.send(emailForm);
//
//            // 유저 엔티티를 찾고, 인증 코드 저장
//            UserEntity user = userRepository.findByEmail(toEmail)
//                    .orElseThrow(() -> new NotFoundException("이메일을 찾을 수 없습니다."));
//
//            user.setVerificationCode(authNum);
//            userRepository.save(user);
//
//            return authNum; // 인증 코드 반환
//        } catch (jakarta.mail.MessagingException e) {
//            log.error("메일 작성 중 오류 발생", e);
//            throw e;
//        }
//    }
//
//
//    // thymeleaf를 통한 html 적용
//    public String setContext(String code, String type) {
//        Context context = new Context();
//        context.setVariable("code", code);
//        return templateEngine.process(type, context);
//    }
//
//    // 이메일 체크
//    public ResponseEntity<String> checkEmail(String memberEmail){
//        boolean exists = userRepository.existsByEmail(memberEmail);
//        if (exists){
//            return ResponseEntity.ok("이메일 인증 완료");
//        } else {
//            throw new NotFoundException("이메일을 찾을 수 없습니다.");
//        }
//    }
//
//}