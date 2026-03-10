package com.mpole.hdt.digitaltwin.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-expiration}") long refreshTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }
    
    /**
     * Access Token 생성
     */
    public String generateAccessToken(String loginId, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);
        
        return Jwts.builder()
                .subject(loginId)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
    
    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(String loginId, String deviceId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);
        
        return Jwts.builder()
                .subject(loginId)
                .claim("type", "refresh")
                .claim("deviceId", deviceId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }
    
    /**
     * 토큰에서 사용자명 추출
     */
    public String getLoginIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 토큰에서 권한 추출
     */
    public List<String> getRoleFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            Object roles = claims.get("roles");

            if (roles instanceof List<?>) {
                // 리스트의 각 요소를 안전하게 String으로 변환
                return ((List<?>) roles).stream()
                        .map(Object::toString)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 토큰 파싱 실패 또는 만료 시 처리 로직
            log.error("유효하지 않은 토큰입니다.", e);
        }

        return Collections.emptyList(); // 기본값으로 빈 리스트 반환
    }

    /**
     * 토큰에서 권한 추출
     */
    public String getDeviceIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("deviceId", String.class);
    }

    /**
     * 토큰 타입 추출 (access / refresh)
     */
    public String getTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    /**
     * Access Token 여부 확인
     */
    public boolean isAccessToken(String token) {
        try {
            String type = getTypeFromToken(token);
            return "access".equals(type);
        } catch (Exception e) {
            log.error("토큰 타입 확인 실패: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token 여부 확인
     */
    public boolean isRefreshToken(String token) {
        try {
            String type = getTypeFromToken(token);
            return "refresh".equals(type);
        } catch (Exception e) {
            log.error("토큰 타입 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
            //return false;
        }
    }
    
    /**
     * 토큰 파싱
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 토큰 만료 시간 조회
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
    
    /**
     * Access Token 만료 시간 (밀리초)
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}

