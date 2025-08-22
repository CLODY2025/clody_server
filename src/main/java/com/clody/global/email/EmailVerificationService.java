package com.clody.global.email;

import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmailService emailService;
    
    @Value("${email.send.enabled:true}")
    private boolean emailSendEnabled;
    
    private static final String EMAIL_VERIFICATION_PREFIX = "email_verification:";
    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final Duration VERIFICATION_CODE_EXPIRATION = Duration.ofMinutes(5);

    public String generateAndSaveVerificationCode(String email) {
        String verificationCode = generateVerificationCode();
        String key = EMAIL_VERIFICATION_PREFIX + email;
        
        // Redis에 인증번호 저장 (5분 만료)
        redisTemplate.opsForValue().set(key, verificationCode, VERIFICATION_CODE_EXPIRATION);
        
        try {
            if (emailSendEnabled) {
                // 실제 이메일 발송
                emailService.sendVerificationEmail(email, verificationCode);
                log.info("실제 이메일 발송 완료 - email: {}", email);
            } else {
                // 개발환경에서는 콘솔에 인증번호 출력
                log.info("=== 이메일 인증번호 (콘솔 출력) ===");
                log.info("이메일: {}", email);
                log.info("인증번호: {}", verificationCode);
                log.info("만료시간: 5분");
                log.info("================================");
            }
        } catch (Exception e) {
            log.error("이메일 발송 실패, 콘솔 로그로 대체 - email: {}, error: {}", email, e.getMessage());
            // 이메일 발송 실패 시 콘솔 로그로 대체
            log.info("=== 이메일 인증번호 (발송 실패, 콘솔 출력) ===");
            log.info("이메일: {}", email);
            log.info("인증번호: {}", verificationCode);
            log.info("만료시간: 5분");
            log.info("==========================================");
        }
        
        return verificationCode;
    }

    public boolean verifyCode(String email, String inputCode) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        String savedCode = (String) redisTemplate.opsForValue().get(key);
        
        if (savedCode == null) {
            log.warn("인증번호가 만료되거나 존재하지 않습니다. email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED);
        }
        
        if (!savedCode.equals(inputCode)) {
            log.warn("인증번호가 일치하지 않습니다. email: {}, input: {}, saved: {}", email, inputCode, savedCode);
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_CODE_INVALID);
        }
        
        // 인증 성공 시 Redis에서 인증번호 삭제
        redisTemplate.delete(key);
        
        // 인증 완료 플래그 저장 (30분 유지)
        String verifiedKey = EMAIL_VERIFICATION_PREFIX + "verified:" + email;
        redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofMinutes(30));
        
        log.info("이메일 인증이 완료되었습니다. email: {}", email);
        return true;
    }

    public boolean isEmailVerified(String email) {
        String verifiedKey = EMAIL_VERIFICATION_PREFIX + "verified:" + email;
        String verified = (String) redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(verified);
    }

    public void removeVerifiedFlag(String email) {
        String verifiedKey = EMAIL_VERIFICATION_PREFIX + "verified:" + email;
        redisTemplate.delete(verifiedKey);
    }

    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }
}