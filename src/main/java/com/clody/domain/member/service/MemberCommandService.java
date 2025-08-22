package com.clody.domain.member.service;

import com.clody.domain.member.dto.MemberRequestDTO;
import com.clody.domain.member.dto.MemberResponseDTO;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.entity.MemberToken;
import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.global.email.EmailVerificationService;
import com.clody.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public MemberResponseDTO.SendEmailVerification sendEmailVerification(MemberRequestDTO.SendEmailVerification request) {
        String email = request.getEmail();
        
        // 이미 가입된 이메일인지 확인
        if (memberRepository.existsByEmail(email)) {
            log.warn("이미 가입된 이메일입니다. email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 인증번호 생성 및 저장
        String verificationCode = emailVerificationService.generateAndSaveVerificationCode(email);
        
        log.info("이메일 인증번호 발송 완료 - email: {}", email);
        
        return MemberResponseDTO.SendEmailVerification.builder()
                .message("인증번호가 이메일로 발송되었습니다")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    public MemberResponseDTO.VerifyEmail verifyEmail(MemberRequestDTO.VerifyEmail request) {
        String email = request.getEmail();
        String verificationCode = request.getVerificationCode();
        
        // 인증번호 검증
        boolean verified = emailVerificationService.verifyCode(email, verificationCode);
        
        if (!verified) {
            log.warn("이메일 인증 실패 - email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_FAILED);
        }
        
        log.info("이메일 인증 완료 - email: {}", email);
        
        return MemberResponseDTO.VerifyEmail.builder()
                .message("이메일 인증이 완료되었습니다")
                .verified(true)
                .build();
    }

    public MemberResponseDTO.SignUp signUp(MemberRequestDTO.SignUp request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        String verificationCode = request.getVerificationCode();
        
        // 최종 이메일 인증 확인 (Redis에서 확인)
        if (!emailVerificationService.isEmailVerified(email)) {
            log.warn("이메일 인증이 완료되지 않았습니다. email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(email)) {
            log.warn("이미 가입된 이메일입니다. email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(nickname)) {
            log.warn("이미 사용중인 닉네임입니다. nickname: {}", nickname);
            throw new MemberException(MemberErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // 회원 생성
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .isEmailVerified(true)
                .build();
        
        Member savedMember = memberRepository.save(member);
        
        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(savedMember.getEmail(), savedMember.getId());
        String refreshToken = jwtUtil.generateRefreshToken(savedMember.getEmail(), savedMember.getId());
        LocalDateTime tokenExpiresAt = jwtUtil.getExpirationAsLocalDateTime(accessToken);
        
        // Refresh Token을 DB에 저장 (MemberToken 엔티티 사용)
        MemberToken memberToken = MemberToken.builder()
                .member(savedMember)
                .refreshToken(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiredAt(jwtUtil.getExpirationAsLocalDateTime(refreshToken))
                .revoked(false)
                .build();
        
        savedMember.getTokens().add(memberToken);
        
        // Redis에서 이메일 인증 플래그 삭제
        emailVerificationService.removeVerifiedFlag(email);
        
        log.info("회원가입 완료 - memberId: {}, email: {}, nickname: {}", 
                savedMember.getId(), savedMember.getEmail(), savedMember.getNickname());
        
        return MemberResponseDTO.SignUp.from(savedMember, accessToken, refreshToken, tokenExpiresAt);
    }
}