package com.clody.global.auth;

import com.clody.global.apiPayload.code.base.FailureCode;
import com.clody.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public void saveRefreshToken(Long memberId, String refreshToken, Date expiration) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        
        // 만료 시간 계산
        Duration ttl = Duration.between(
                LocalDateTime.now(),
                expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
        
        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set(key, refreshToken, ttl);
        
        log.debug("RefreshToken 저장 완료 - memberId: {}, expiration: {}", memberId, expiration);
    }

    public String getRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        String refreshToken = (String) redisTemplate.opsForValue().get(key);
        
        if (refreshToken == null) {
            log.warn("RefreshToken을 찾을 수 없습니다. memberId: {}", memberId);
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        }
        
        return refreshToken;
    }

    public boolean validateRefreshToken(Long memberId, String refreshToken) {
        try {
            String savedToken = getRefreshToken(memberId);
            boolean isValid = savedToken.equals(refreshToken);
            
            if (!isValid) {
                log.warn("RefreshToken이 일치하지 않습니다. memberId: {}", memberId);
            }
            
            return isValid;
        } catch (GeneralException e) {
            return false;
        }
    }

    public void deleteRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        Boolean deleted = redisTemplate.delete(key);
        
        log.debug("RefreshToken 삭제 - memberId: {}, deleted: {}", memberId, deleted);
    }

    public void deleteAllRefreshTokens(Long memberId) {
        // 현재는 사용자당 하나의 RefreshToken만 관리하므로 동일한 동작
        deleteRefreshToken(memberId);
    }

    public boolean existsRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}