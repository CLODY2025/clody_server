package com.clody.domain.member.service;

import com.clody.domain.member.dto.MemberRequestDTO;
import com.clody.domain.member.dto.MemberResponseDTO;
import com.clody.domain.member.entity.AccountScope;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.entity.MemberToken;
import com.clody.domain.member.exception.MemberErrorCode;
import com.clody.domain.member.exception.MemberException;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.global.email.EmailVerificationService;
import com.clody.global.jwt.JwtUtil;
import com.clody.global.s3.service.S3Service;
import com.clody.global.util.ImageValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final S3Service s3Service;

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

    public MemberResponseDTO.UpdateAccountScope updateAccountScope(Member member, MemberRequestDTO.UpdateAccountScope request) {
        AccountScope newAccountScope = request.getAccountScope();
        
        log.info("계정 범위 변경 시도 - memberId: {}, 현재: {}, 변경: {}", 
                member.getId(), member.getAccountScope(), newAccountScope);
        
        // 현재와 동일한 설정인지 확인
        if (member.getAccountScope() == newAccountScope) {
            log.info("동일한 계정 범위로 변경 시도 - memberId: {}, accountScope: {}", 
                    member.getId(), newAccountScope);
            throw new MemberException(MemberErrorCode.SAME_ACCOUNT_SCOPE);
        }
        
        // 계정 범위 업데이트
        member.updateAccountScope(newAccountScope);
        Member savedMember = memberRepository.save(member);
        
        log.info("계정 범위 변경 완료 - memberId: {}, accountScope: {}", 
                savedMember.getId(), savedMember.getAccountScope());
        
        return MemberResponseDTO.UpdateAccountScope.builder()
                .memberId(savedMember.getId())
                .accountScope(savedMember.getAccountScope())
                .message(getAccountScopeMessage(newAccountScope))
                .build();
    }
    
    private String getAccountScopeMessage(AccountScope accountScope) {
        return switch (accountScope) {
            case PUBLIC -> "계정이 공개로 설정되었습니다";
            case FOLLOWERS_ONLY -> "계정이 팔로워만 보기로 설정되었습니다";
        };
    }

    public MemberResponseDTO.SendPasswordResetVerification sendPasswordResetVerification(MemberRequestDTO.SendPasswordResetVerification request) {
        String email = request.getEmail();
        
        // 가입된 이메일인지 확인
        if (!memberRepository.existsByEmail(email)) {
            log.warn("존재하지 않는 이메일로 비밀번호 변경 요청 - email: {}", email);
            throw new MemberException(MemberErrorCode.NOT_FOUND);
        }
        
        // 인증번호 생성 및 저장 (비밀번호 변경용)
        String verificationCode = emailVerificationService.generateAndSavePasswordResetCode(email);
        
        log.info("비밀번호 변경용 인증번호 발송 완료 - email: {}", email);
        
        return MemberResponseDTO.SendPasswordResetVerification.builder()
                .message("비밀번호 변경용 인증번호가 이메일로 발송되었습니다")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    public MemberResponseDTO.VerifyPasswordResetCode verifyPasswordResetCode(MemberRequestDTO.VerifyPasswordResetCode request) {
        String email = request.getEmail();
        String verificationCode = request.getVerificationCode();
        
        // 가입된 이메일인지 확인
        if (!memberRepository.existsByEmail(email)) {
            log.warn("존재하지 않는 이메일로 비밀번호 변경 인증 시도 - email: {}", email);
            throw new MemberException(MemberErrorCode.NOT_FOUND);
        }
        
        // 인증번호 검증 (비밀번호 변경용)
        boolean verified = emailVerificationService.verifyPasswordResetCode(email, verificationCode);
        
        if (!verified) {
            log.warn("비밀번호 변경용 인증번호 검증 실패 - email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_VERIFICATION_FAILED);
        }
        
        log.info("비밀번호 변경용 인증번호 검증 완료 - email: {}", email);
        
        return MemberResponseDTO.VerifyPasswordResetCode.builder()
                .message("인증번호 확인이 완료되었습니다")
                .verified(true)
                .build();
    }

    public MemberResponseDTO.ChangePassword changePassword(Member member, MemberRequestDTO.ChangePassword request) {
        String newPassword = request.getNewPassword();
        String verificationCode = request.getVerificationCode();
        String email = member.getEmail();
        
        log.info("비밀번호 변경 시도 - memberId: {}, email: {}", member.getId(), email);
        
        // 인증번호 최종 확인
        if (!emailVerificationService.isPasswordResetVerified(email)) {
            log.warn("인증번호 검증이 완료되지 않은 비밀번호 변경 시도 - email: {}", email);
            throw new MemberException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }
        
        // 현재 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            log.warn("현재 비밀번호와 동일한 비밀번호로 변경 시도 - memberId: {}", member.getId());
            throw new MemberException(MemberErrorCode.SAME_PASSWORD);
        }
        
        // 비밀번호 변경
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        member.updatePassword(encodedNewPassword);
        Member savedMember = memberRepository.save(member);
        
        // 비밀번호 변경용 인증 플래그 삭제
        emailVerificationService.removePasswordResetVerifiedFlag(email);
        
        log.info("비밀번호 변경 완료 - memberId: {}", savedMember.getId());
        
        return MemberResponseDTO.ChangePassword.builder()
                .memberId(savedMember.getId())
                .message("비밀번호가 성공적으로 변경되었습니다")
                .changedAt(LocalDateTime.now())
                .build();
    }

    public MemberResponseDTO.UploadProfileImage uploadProfileImage(Member member, MultipartFile imageFile) {
        // 이미지 파일 검증
        ImageValidationUtil.validateImageFile(imageFile);
        
        log.info("프로필 이미지 업로드 시작 - memberId: {}, 파일명: {}, 크기: {}bytes", 
                member.getId(), imageFile.getOriginalFilename(), imageFile.getSize());

        try {
            // 기존 프로필 이미지가 있다면 삭제
            String existingImageUrl = member.getProfileImageUrl();
            if (existingImageUrl != null && !existingImageUrl.trim().isEmpty()) {
                String existingKey = extractS3KeyFromUrl(existingImageUrl);
                if (existingKey != null) {
                    try {
                        s3Service.deleteFile(existingKey);
                        log.info("기존 프로필 이미지 삭제 완료 - key: {}", existingKey);
                    } catch (Exception e) {
                        log.warn("기존 프로필 이미지 삭제 실패 - key: {}, error: {}", existingKey, e.getMessage());
                    }
                }
            }

            // 새 이미지 업로드
            String imageKey = ImageValidationUtil.generateProfileImageKey(member.getId(), imageFile.getOriginalFilename());
            String imageUrl = s3Service.uploadFile(imageFile, imageKey);
            
            // 회원 정보 업데이트
            member.updateProfileImageUrl(imageUrl);
            Member savedMember = memberRepository.save(member);
            
            log.info("프로필 이미지 업로드 완료 - memberId: {}, imageUrl: {}", 
                    savedMember.getId(), imageUrl);

            return MemberResponseDTO.UploadProfileImage.builder()
                    .memberId(savedMember.getId())
                    .profileImageUrl(imageUrl)
                    .message("프로필 이미지가 성공적으로 업데이트되었습니다")
                    .uploadedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패 - memberId: {}, error: {}", member.getId(), e.getMessage(), e);
            throw new MemberException(MemberErrorCode.INVALID_IMAGE_FILE);
        }
    }

    private String extractS3KeyFromUrl(String s3Url) {
        if (s3Url == null || s3Url.trim().isEmpty()) {
            return null;
        }
        
        try {
            // S3 URL에서 키 추출 (예: https://bucket.s3.region.amazonaws.com/key -> key)
            if (s3Url.contains(".amazonaws.com/")) {
                int keyStartIndex = s3Url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
                return s3Url.substring(keyStartIndex);
            }
            return null;
        } catch (Exception e) {
            log.warn("S3 URL에서 키 추출 실패 - url: {}, error: {}", s3Url, e.getMessage());
            return null;
        }
    }
}