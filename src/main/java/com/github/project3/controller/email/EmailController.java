//package com.github.project3.controller.email;
//import com.github.project3.dto.email.EmailRequest;
//import com.github.project3.service.email.EmailService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.MessagingException;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.UnsupportedEncodingException;
//
//@RequestMapping("/api/email")
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class EmailController {
//    private final EmailService emailService;
//
//    @PostMapping("login/mailConfirm")
//    public String mailConfirm(@RequestBody EmailRequest emailDto) throws MessagingException, UnsupportedEncodingException {
//
//        String authCode = emailService.sendEmail(emailDto.getEmail());
//        return authCode;
//    }
//}
