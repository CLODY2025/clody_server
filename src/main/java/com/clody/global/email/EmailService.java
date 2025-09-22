package com.clody.global.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            // 발신자, 수신자 설정
            helper.setFrom(emailProperties.getFrom());
            helper.setTo(toEmail);
            helper.setSubject(emailProperties.getVerification().getSubject());

            // HTML 템플릿 읽기 및 인증번호 치환
            String htmlContent = loadEmailTemplate(verificationCode);
            helper.setText(htmlContent, true);

            // 이메일 발송
            mailSender.send(message);
            
            log.info("=== 이메일 인증번호 발송 완료 ===");
            log.info("수신자: {}", toEmail);
            log.info("인증번호: {}", verificationCode);
            log.info("발송 시간: {}", java.time.LocalDateTime.now());
            log.info("===============================");

        } catch (Exception e) {
            log.error("이메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다", e);
        }
    }

    private String loadEmailTemplate(String verificationCode) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/verification-code.html");
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
            
            // 템플릿 변수 치환
            return content.replace("${verificationCode}", verificationCode);
            
        } catch (IOException e) {
            log.error("이메일 템플릿 로드 실패: {}", e.getMessage(), e);
            // 템플릿 로드 실패 시 기본 텍스트 반환
            return createFallbackEmailContent(verificationCode);
        } catch (Exception e) {
            log.error("이메일 템플릿 처리 중 오류: {}", e.getMessage(), e);
            return createFallbackEmailContent(verificationCode);
        }
    }

    private String createFallbackEmailContent(String verificationCode) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; }
                    .header { text-align: center; color: #4a90e2; font-size: 24px; font-weight: bold; margin-bottom: 20px; }
                    .code { text-align: center; font-size: 32px; font-weight: bold; color: #4a90e2; padding: 20px; background: #f8f9fa; border-radius: 8px; margin: 20px 0; }
                    .message { color: #333; font-size: 16px; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">🎨 Clody</div>
                    <div class="message">
                        <p>안녕하세요!</p>
                        <p>Clody 회원가입을 위한 이메일 인증번호입니다.</p>
                        <p>아래의 6자리 인증번호를 입력해주세요.</p>
                    </div>
                    <div class="code">""" + verificationCode + """
                    </div>
                    <div class="message">
                        <p><strong>⚠️ 중요:</strong> 이 인증번호는 5분 후에 만료됩니다.</p>
                        <p>이 인증번호는 누구에게도 공유하지 마세요.</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 Clody. All rights reserved.</p>
                        <p>이 메일은 발신전용입니다.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    public void sendTestEmail(String toEmail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(toEmail);
            helper.setSubject("[Clody] 이메일 연결 테스트");

            String testContent = String.format("""
            <h2>🎉 이메일 연결 테스트 성공!</h2>
            <p>Clody 이메일 시스템이 정상적으로 작동하고 있습니다.</p>
            <p>발송 시간: %s</p>
            <hr>
            <p style="color: #999; font-size: 12px;">© 2024 Clody. 이 메일은 테스트용입니다.</p>
            """, java.time.LocalDateTime.now());

            helper.setText(testContent, true);
            mailSender.send(message);

            log.info("테스트 이메일 발송 완료: {}", toEmail);

        } catch (Exception e) {
            log.error("테스트 이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("테스트 이메일 발송 실패", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(emailProperties.getFrom());
            helper.setTo(toEmail);
            helper.setSubject("[CLODY] 비밀번호 변경 인증번호");

            String htmlContent = createPasswordResetEmailContent(verificationCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            
            log.info("=== 비밀번호 변경용 이메일 인증번호 발송 완료 ===");
            log.info("수신자: {}", toEmail);
            log.info("인증번호: {}", verificationCode);
            log.info("발송 시간: {}", java.time.LocalDateTime.now());
            log.info("===============================================");

        } catch (Exception e) {
            log.error("비밀번호 변경용 이메일 발송 실패 - 수신자: {}, 오류: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("비밀번호 변경용 이메일 발송에 실패했습니다", e);
        }
    }

    private String createPasswordResetEmailContent(String verificationCode) {
        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; }
                    .header { text-align: center; color: #4a90e2; font-size: 24px; font-weight: bold; margin-bottom: 20px; }
                    .code { text-align: center; font-size: 32px; font-weight: bold; color: #e74c3c; padding: 20px; background: #f8f9fa; border-radius: 8px; margin: 20px 0; }
                    .message { color: #333; font-size: 16px; }
                    .warning { background: #fff3cd; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">🔒 Clody</div>
                    <div class="message">
                        <p>안녕하세요!</p>
                        <p>비밀번호 변경을 위한 인증번호입니다.</p>
                        <p>아래의 6자리 인증번호를 입력해주세요.</p>
                    </div>
                    <div class="code">""" + verificationCode + """
                    </div>
                    <div class="warning">
                        <p><strong>⚠️ 보안 안내:</strong></p>
                        <ul>
                            <li>이 인증번호는 5분 후에 만료됩니다</li>
                            <li>본인이 요청하지 않았다면 무시하세요</li>
                            <li>이 인증번호는 누구에게도 공유하지 마세요</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>© 2024 Clody. All rights reserved.</p>
                        <p>이 메일은 발신전용입니다.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

}