package com.mpole.hdt.digitaltwin.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 세션 사용 안함
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/actuator/**").permitAll()  // 운영 시 hasRole('ADMIN')으로 변경
                        .requestMatchers(
                                "/api/auth/login",          // 로그인
                                "/api/auth/signup",         // 회원가입
                                "/api/auth/refresh",        // 토큰 갱신
                                "/api/auth/health",         // 헬스체크
                                "/api/digitaltwin/health"   // 헬스체크
                        ).permitAll()
                        .requestMatchers(
                                "/stomp/**",
                                "/ws/**"
                        ).permitAll()
                        .requestMatchers(
                                "/*.html",                  // HTML 파일 (개발용)
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/equipment/categories/**",  // 장비 카테고리 API (임시 전체 허용)
                                "/api/categories/**",            // 하이브리드 방식 카테고리 API (임시 전체 허용)
                                "/api/assets/**"                 // 하이브리드 방식 에셋 API (임시 전체 허용)
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/me",             // 내 정보 조회
                                "/api/auth/logout",         // 로그아웃
                                "/api/auth/change-password" // 비밀번호 변경
                        ).authenticated()
                        .requestMatchers(
                                "/api/auth/unlock/**"       // 계정 잠금 해제
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                "/api/digitaltwin/status",  // 상태 조회
                                "/api/digitaltwin/data"     // 데이터 조회
                        ).hasAnyRole("ADMIN", "MANAGER", "USER")
                        //.anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                // H2 Console을 위한 Frame 설정
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}

