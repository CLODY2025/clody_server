package com.clody.global.jwt;

import com.clody.global.apiPayload.code.base.FailureCode;
import com.clody.global.apiPayload.exception.GeneralException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String email, Long memberId) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("memberId", memberId)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email, Long memberId) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpiration());
        
        return Jwts.builder()
                .setSubject(email)
                .claim("memberId", memberId)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new GeneralException(FailureCode.JWT_EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new GeneralException(FailureCode.JWT_UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw new GeneralException(FailureCode.JWT_INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token claims string is empty: {}", e.getMessage());
            throw new GeneralException(FailureCode.JWT_CLAIMS_EMPTY);
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public Long getMemberIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("memberId", Long.class);
    }

    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    public LocalDateTime getExpirationAsLocalDateTime(String token) {
        Date expiration = getExpirationFromToken(token);
        return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (GeneralException e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (GeneralException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }
}