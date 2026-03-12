package com.mpole.hdt.digitaltwin.service;

import com.mpole.hdt.digitaltwin.persistence.device.DeviceBulkSyncRepository;
import com.mpole.hdt.digitaltwin.external.model.ExternalMSViewEntity;
import com.mpole.hdt.digitaltwin.external.repository.MssqlRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncService {
    @PersistenceContext(unitName = "main") // PostgreSQL
    private final EntityManager entityManager;

    private final DeviceBulkSyncRepository deviceBulkSyncRepository;
    private final MssqlRepository mssqlRepository;

    @Transactional
    public void syncInBatches() {
        int batchSize = 10000;
        long totalCount = mssqlRepository.count(); // 전체 건수 확인
        int totalPages = (int) Math.ceil((double) totalCount / batchSize);

        for (int i = 0; i < totalPages; i++) {
            // 1. MSSQL에서 1만 건 읽기
            List<ExternalMSViewEntity> batchData = mssqlRepository.findAll(PageRequest.of(i, batchSize)).getContent();

            // 2. PostgreSQL에 Upsert (Native Query나 JdbcTemplate 활용)
            deviceBulkSyncRepository.bulkUpsertDevices(batchData,batchData.size());

            // 3. 메모리 비우기 (매우 중요: 10만 건이 메모리에 쌓이는 것 방지)
            entityManager.flush();
            entityManager.clear();
        }
    }

}
