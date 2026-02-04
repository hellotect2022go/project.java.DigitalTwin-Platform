package com.mpole.hdt.digitaltwin.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public record UserPrincipal(String loginId, String deviceId, Collection<? extends GrantedAuthority> authorities){}

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
//                // 🔐 토큰 타입 검증: Access Token만 인증 처리
//                if (!jwtTokenProvider.isAccessToken(token)) {
//                    log.warn("⚠️ Refresh Token이 Authorization 헤더에 사용됨. Access Token만 사용 가능합니다.");
//                    filterChain.doFilter(request, response);
//                    return;
//                }


                String loginId = jwtTokenProvider.getLoginIdFromToken(token);
                String role = jwtTokenProvider.getRoleFromToken(token);
                String deviceId = jwtTokenProvider.getDeviceIdFromToken(token);

                List<SimpleGrantedAuthority> authorities = (role != null)
                        ? Collections.singletonList(new SimpleGrantedAuthority(role))
                        : Collections.emptyList(); // 권한이 없으면 빈 리스트

                UserPrincipal principal = new UserPrincipal(loginId, deviceId, authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                principal.authorities()
                                //Collections.singletonList(new SimpleGrantedAuthority(role))
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("✅ 사용자 인증 완료: {}, 권한: {}", loginId, role);
            }
        } catch (Exception e) {
            log.error("❌ 인증 처리 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}

