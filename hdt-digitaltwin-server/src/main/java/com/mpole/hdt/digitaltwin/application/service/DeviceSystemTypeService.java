package com.mpole.hdt.digitaltwin.application.service;

import com.mpole.hdt.digitaltwin.api.dto.request.DeviceSystemTypeRequest;
import com.mpole.hdt.digitaltwin.api.dto.systemtype.DeviceSystemTypeDTO;
import com.mpole.hdt.digitaltwin.application.repository.DeviceSystemTypeRepository;
import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceSystemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceSystemTypeService {

    private final DeviceSystemTypeRepository systemTypeRepository;

    /**
     * 시스템 타입 생성
     */
    @Transactional
    public DeviceSystemTypeDTO createSystemType(DeviceSystemTypeRequest request) {
        // 중복 체크
        if (systemTypeRepository.existsBySysCode(request.getSysCode())) {
            throw new IllegalArgumentException("이미 존재하는 시스템 코드입니다: " + request.getSysCode());
        }

        DeviceSystemType systemType = DeviceSystemType.builder()
                .sysCode(request.getSysCode())
                .sysNameKo(request.getSysNameKo())
                .sysNameEn(request.getSysNameEn())
                .description(request.getDescription())
                .iconUrl(request.getIconUrl())
                .enabled(request.getEnabled())
                .build();

        systemType = systemTypeRepository.save(systemType);
        log.info("시스템 타입 생성: {} (코드: {})", systemType.getSysNameKo(), systemType.getSysCode());

        return toDto(systemType);
    }

    /**
     * 시스템 타입 수정
     */
    @Transactional
    public DeviceSystemTypeDTO updateSystemType(Long systemId, DeviceSystemTypeRequest request) {
        DeviceSystemType systemType = systemTypeRepository.findById(systemId)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + systemId));

        // 시스템 코드 중복 체크 (자기 자신 제외)
        systemTypeRepository.findBySysCode(request.getSysCode())
                .filter(st -> !st.getId().equals(systemId))
                .ifPresent(st -> {
                    throw new IllegalArgumentException("이미 존재하는 시스템 코드입니다: " + request.getSysCode());
                });

        systemType.setSysCode(request.getSysCode());
        systemType.setSysNameKo(request.getSysNameKo());
        systemType.setSysNameEn(request.getSysNameEn());
        systemType.setDescription(request.getDescription());
        systemType.setIconUrl(request.getIconUrl());
        systemType.setEnabled(request.getEnabled());

        systemType = systemTypeRepository.save(systemType);
        log.info("시스템 타입 수정: {} (ID: {})", systemType.getSysNameKo(), systemId);

        return toDto(systemType);
    }

    /**
     * 시스템 타입 삭제
     */
    @Transactional
    public void deleteSystemType(Long systemId) {
        DeviceSystemType systemType = systemTypeRepository.findById(systemId)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + systemId));

        // TODO: 연결된 Asset이 있는지 확인하는 로직 추가 가능
        systemTypeRepository.delete(systemType);
        log.info("시스템 타입 삭제: {} (ID: {})", systemType.getSysNameKo(), systemId);
    }

    /**
     * 시스템 타입 단건 조회 (ID)
     */
    @Transactional(readOnly = true)
    public DeviceSystemTypeDTO getSystemType(Long systemId) {
        DeviceSystemType systemType = systemTypeRepository.findById(systemId)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + systemId));
        return toDto(systemType);
    }

    /**
     * 시스템 타입 조회 (코드)
     */
    @Transactional(readOnly = true)
    public DeviceSystemTypeDTO getSystemTypeByCode(String sysCode) {
        DeviceSystemType systemType = systemTypeRepository.findBySysCode(sysCode)
                .orElseThrow(() -> new IllegalArgumentException("시스템 타입을 찾을 수 없습니다: " + sysCode));
        return toDto(systemType);
    }

    /**
     * 전체 시스템 타입 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DeviceSystemTypeDTO> getAllSystemTypes() {
        return systemTypeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 시스템 타입 목록 조회 (정렬된)
     */
    @Transactional(readOnly = true)
    public List<DeviceSystemTypeDTO> getEnabledSystemTypes() {
        return systemTypeRepository.findByEnabledTrueOrderBySysCode().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Entity -> DTO 변환
     */
    private DeviceSystemTypeDTO toDto(DeviceSystemType systemType) {
        return DeviceSystemTypeDTO.builder()
                .systemId(systemType.getId())
                .systemCode(systemType.getSysCode())
                .systemNameKo(systemType.getSysNameKo())
                .systemNameEn(systemType.getSysNameEn())
                .description(systemType.getDescription())
                .iconUrl(systemType.getIconUrl())
                .enabled(systemType.getEnabled())
                .createdAt(systemType.getCreatedAt())
                .updatedAt(systemType.getUpdatedAt())
                .build();
    }
}

