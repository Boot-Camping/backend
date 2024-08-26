//package com.github.project3.controller.email;
//import com.github.project3.dto.email.EmailRequest;
//import com.github.project3.service.email.EmailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RequestMapping("/api/email")
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class EmailController {
//    private final EmailService emailService;
//
//    // 이메일 체크
//    @GetMapping("/check-email")
//    public ResponseEntity checkEmail(@RequestBody EmailRequest emailRequest){
//
//        String memberEmail = emailRequest.getEmail();
//        log.info("checkEmail 진입", memberEmail);
//        ResponseEntity<String> exists = emailService.checkEmail(memberEmail);
//        return ResponseEntity.ok(exists);
//    }
//
//}
