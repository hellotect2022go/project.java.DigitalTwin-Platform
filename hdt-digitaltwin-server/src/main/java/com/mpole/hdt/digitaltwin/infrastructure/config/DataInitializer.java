package com.mpole.hdt.digitaltwin.infrastructure.config;

import com.mpole.hdt.digitaltwin.application.repository.entity.User;
import com.mpole.hdt.digitaltwin.application.repository.entity.UserRole;
import com.mpole.hdt.digitaltwin.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeUsers(); // 사용자 초기화
    }

    private void initializeUsers() {
        if (userRepository.count() > 0) {
            log.info("사용자 데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        log.info("===== 테스트 사용자 데이터 초기화 시작 =====");

        // 관리자 계정
        User admin = User.builder()
                .loginId("admin")
                .password(passwordEncoder.encode("1q2w3e"))
                .email("dhhan@mpole.co.kr")
                .name("시스템 관리자")
                .role(UserRole.ROLE_ADMIN)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .lastPasswordChangeDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);

        // 매니저 계정
        User manager = User.builder()
                .loginId("manager")
                .password(passwordEncoder.encode("1q2w3e"))
                .email("dhhan@mpole.co.kr")
                .name("운영 매니저")
                .role(UserRole.ROLE_MANAGER)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .lastPasswordChangeDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(manager);

        // Unity 테스트 계정
        User unityUser = User.builder()
                .loginId("unity")
                .password(passwordEncoder.encode("1q2w3e"))
                .email("dhhan@mpole.co.kr")
                .name("Unity 테스트")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .lastPasswordChangeDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(unityUser);

        // 비밀번호 만료 테스트 계정 (90일 이전에 변경)
        User expiredUser = User.builder()
                .loginId("expired")
                .password(passwordEncoder.encode("1q2w3e"))
                .email("expired@hanadream.com")
                .name("만료 테스트")
                .role(UserRole.ROLE_USER)
                .enabled(true)
                .accountNonLocked(true)
                .failedLoginAttempts(0)
                .lastPasswordChangeDate(LocalDateTime.now().minusDays(91))
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(expiredUser);

        log.info("===== 테스트 사용자 {} 건 초기화 완료 =====", userRepository.count());
        log.info("테스트 계정:");
        log.info("  - admin / 1q2w3e (관리자)");
        log.info("  - manager / 1q2w3e (매니저)");
        log.info("  - unity / 1q2w3e (일반 사용자)");
        log.info("  - expired / 1q2w3e (비밀번호 만료 테스트)");
    }
}

