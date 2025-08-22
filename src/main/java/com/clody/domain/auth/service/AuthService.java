package com.clody.domain.auth.service;

import com.clody.domain.auth.dto.AuthRequestDTO;
import com.clody.domain.auth.dto.AuthResponseDTO;
import com.clody.domain.auth.exception.AuthErrorCode;
import com.clody.domain.auth.exception.AuthException;
import com.clody.domain.member.entity.Member;
import com.clody.domain.member.repository.MemberRepository;
import com.clody.global.auth.RefreshTokenService;
import com.clody.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthResponseDTO.Login login(AuthRequestDTO.Login request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 사용자 조회 (이메일 인증된 사용자만)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이메일입니다. email: {}", email);
                    return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                });

        // 이메일 인증 여부 확인
        if (!member.getIsEmailVerified()) {
            log.warn("이메일 인증이 완료되지 않은 사용자입니다. email: {}", email);
            throw new AuthException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            log.warn("비밀번호가 일치하지 않습니다. email: {}", email);
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // JWT 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail(), member.getId());
        LocalDateTime tokenExpiresAt = jwtUtil.getExpirationAsLocalDateTime(accessToken);

        // 기존 RefreshToken이 있다면 삭제
        if (refreshTokenService.existsRefreshToken(member.getId())) {
            refreshTokenService.deleteRefreshToken(member.getId());
        }

        // Redis에 새 RefreshToken 저장
        Date refreshTokenExpiration = jwtUtil.getExpirationFromToken(refreshToken);
        refreshTokenService.saveRefreshToken(member.getId(), refreshToken, refreshTokenExpiration);

        log.info("로그인 성공 - memberId: {}, email: {}", member.getId(), member.getEmail());

        return AuthResponseDTO.Login.of(accessToken, refreshToken, tokenExpiresAt, member);
    }

    public AuthResponseDTO.Logout logout(Long memberId) {
        // Redis에서 RefreshToken 삭제
        refreshTokenService.deleteRefreshToken(memberId);

        log.info("로그아웃 완료 - memberId: {}", memberId);

        return AuthResponseDTO.Logout.of("로그아웃되었습니다");
    }

    public AuthResponseDTO.RefreshToken refreshToken(AuthRequestDTO.RefreshToken request) {
        String refreshToken = request.getRefreshToken();

        try {
            // RefreshToken 검증
            if (!jwtUtil.validateToken(refreshToken)) {
                log.warn("유효하지 않은 RefreshToken입니다");
                throw new AuthException(AuthErrorCode.INVALID_TOKEN);
            }

            // RefreshToken 타입 확인
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                log.warn("RefreshToken이 아닙니다");
                throw new AuthException(AuthErrorCode.INVALID_TOKEN);
            }

            // RefreshToken에서 사용자 정보 추출
            Long memberId = jwtUtil.getMemberIdFromToken(refreshToken);
            String email = jwtUtil.getEmailFromToken(refreshToken);

            // Redis에 저장된 RefreshToken과 비교
            if (!refreshTokenService.validateRefreshToken(memberId, refreshToken)) {
                log.warn("Redis에 저장된 RefreshToken과 일치하지 않습니다. memberId: {}", memberId);
                throw new AuthException(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
            }

            // 사용자 존재 여부 확인
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.warn("존재하지 않는 사용자입니다. memberId: {}", memberId);
                        return new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
                    });

            // 새로운 토큰 생성
            String newAccessToken = jwtUtil.generateAccessToken(email, memberId);
            String newRefreshToken = jwtUtil.generateRefreshToken(email, memberId);
            LocalDateTime tokenExpiresAt = jwtUtil.getExpirationAsLocalDateTime(newAccessToken);

            // 기존 RefreshToken 삭제 후 새 토큰 저장
            refreshTokenService.deleteRefreshToken(memberId);
            Date newRefreshTokenExpiration = jwtUtil.getExpirationFromToken(newRefreshToken);
            refreshTokenService.saveRefreshToken(memberId, newRefreshToken, newRefreshTokenExpiration);

            log.info("토큰 재발급 완료 - memberId: {}, email: {}", memberId, email);

            return AuthResponseDTO.RefreshToken.of(newAccessToken, newRefreshToken, tokenExpiresAt);

        } catch (Exception e) {
            log.error("토큰 재발급 실패: {}", e.getMessage(), e);
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}