package com.finalProject.Back.controller;

import com.finalProject.Back.dto.request.email.ReqEmail;
import com.finalProject.Back.dto.request.email.ReqSendEmailDto;
import com.finalProject.Back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class EmailController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserService userService;

    private Map<String, String> verificationCodes = new HashMap<>();

    @PostMapping("/mail/send")
    public ResponseEntity<String> sendVerificationCode(@RequestBody ReqEmail emailRequest) {
        String email = emailRequest.getEmail();
        String verificationCode = generateVerificationCode();
        // 이메일 전송 로직
        try {
            sendEmail(email, verificationCode);
            return ResponseEntity.ok(verificationCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 실패");
        }
    }

    @PostMapping("/mail/find/send/{email}")
        public ResponseEntity<String> sendEmailCode(@PathVariable String email, @RequestBody ReqSendEmailDto reqSendEmailDto) {
        if(reqSendEmailDto.getValue().equals("FindUser")|| reqSendEmailDto.getValue().equals("FindPassword")){
            try{
                sendUsername(email, reqSendEmailDto.getUsername());
                return ResponseEntity.ok("메일 전송 성공");
            }catch (Exception e){
                System.out.println("메일 전송 실패");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 실패");
            }
        }else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 실패");
        }
    }

    private void sendEmail(String email, String verificationCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            helper.setTo(email);
            helper.setSubject("요청 결과");

            String emailTemplate = loadEmailTemplate(email , verificationCode);
            helper.setText(emailTemplate, true);
            System.out.println(verificationCode);

            Resource resource = new ClassPathResource("templates/CoffeeShop/CAFEINBUSAN1.png");
            FileSystemResource logoImage = new FileSystemResource(resource.getFile());

            helper.addInline("logoImage", logoImage);

            javaMailSender.send(message);
            System.out.println("메일 전송 성공");
        } catch (Exception e) {
            System.out.println("메일 전송 실패! 오류: " + e.getMessage());
        }
    }

    private String generateVerificationCode() {
        // 인증 코드 생성 로직 (예: 6자리 랜덤 숫자)
        return String.valueOf(new Random().nextInt(999999));
    }

    private void sendUsername(String email, String username) throws IOException {
        System.out.println("아이디: " + username);
        System.out.println("이메일: " + email);
        System.out.println("메일 출발");

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

            helper.setTo(email);
            helper.setSubject("요청 결과");

            String emailTemplate = loadEmailTemplate(email ,username);
            helper.setText(emailTemplate, true);

            Resource resource = new ClassPathResource("templates/CoffeeShop/CAFEINBUSAN1.png");
            FileSystemResource logoImage = new FileSystemResource(resource.getFile());

            helper.addInline("logoImage", logoImage);

            javaMailSender.send(message);
            System.out.println("메일 전송 성공");
        } catch (Exception e) {
            System.out.println("메일 전송 실패! 오류: " + e.getMessage());
        }
    }

    private String loadEmailTemplate(String email ,String username) throws IOException {
        if(email.contains("@gmail.com")){
            Resource resource = new ClassPathResource("templates/emailTemplateGoogle.html");
            String content = new String(Files.readAllBytes(resource.getFile().toPath()));
            return content.replace("${value}", username);
        }
        Resource resource = new ClassPathResource("templates/emailTemplate.html");
        String content = new String(Files.readAllBytes(resource.getFile().toPath()));
        return content.replace("${value}", username);
    }


}