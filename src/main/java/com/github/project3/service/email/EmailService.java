package com.github.project3.service.email;


import com.github.project3.dto.email.EmailRequest;
import com.github.project3.entity.user.UserEntity;
import com.github.project3.repository.user.UserRepository;
import com.github.project3.service.exceptions.NotAcceptException;
import com.github.project3.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;

    // 이메일 체크
    public ResponseEntity<String> checkEmail(String memberEmail){
        boolean exists = userRepository.existsByEmail(memberEmail);
        if (exists){
            return ResponseEntity.ok("이메일 인증 완료");
        } else {
            throw new NotFoundException("이메일을 찾을 수 없습니다.");
        }
    }


//    // 인증번호 및 임시 비밀번호 생성 메서드
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
//    // thymeleaf를 통한 html 적용
//    public String setContext(String code, String type) {
//        Context context = new Context();
//        context.setVariable("code", code);
//        return templateEngine.process(type, context);
//    }
}