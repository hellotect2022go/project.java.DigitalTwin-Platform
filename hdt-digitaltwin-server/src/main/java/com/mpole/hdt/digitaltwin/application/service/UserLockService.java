package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.application.repository.UserRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 계정 잠금 전용 서비스
 * 로그인 실패 시 즉시 커밋되도록 별도 트랜잭션 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLockService {

    private final UserRepository userRepository;
    private static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * 로그인 실패 처리 (독립적인 새로운 트랜잭션)
     * REQUIRES_NEW: 부모 트랜잭션과 무관하게 즉시 커밋
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleLoginFailure(User user) {
        log.debug("로그인 실패 처리 시작: {}, 현재 실패 횟수: {}", user.getLoginId(), user.getFailedLoginAttempts());
        
        user.incrementFailedAttempts();

        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.lockAccount();
            log.warn("⚠️ 계정 잠금: {} (로그인 실패 {}회)", user.getLoginId(), user.getFailedLoginAttempts());
        } else {
            log.info("로그인 실패 횟수 증가: {} ({}회)", user.getLoginId(), user.getFailedLoginAttempts());
        }

        User savedUser = userRepository.save(user);
        log.debug("DB 저장 완료: failedLoginAttempts={}, accountNonLocked={}", 
                savedUser.getFailedLoginAttempts(), savedUser.getAccountNonLocked());
    }

    /**
     * 계정 잠금 해제
     */
    @Transactional
    public void unlockAccount(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        user.setAccountNonLocked(true);
        user.resetFailedAttempts();
        userRepository.save(user);

        log.info("✅ 계정 잠금 해제: {}", loginId);
    }
}



