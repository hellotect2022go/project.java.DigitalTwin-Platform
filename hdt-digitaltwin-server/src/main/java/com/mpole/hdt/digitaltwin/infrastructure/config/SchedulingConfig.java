package com.mpole.hdt.digitaltwin.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄링 설정
 * 실시간 데이터 전송을 위한 스케줄러 활성화
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // @Scheduled 어노테이션이 동작하도록 활성화
}

