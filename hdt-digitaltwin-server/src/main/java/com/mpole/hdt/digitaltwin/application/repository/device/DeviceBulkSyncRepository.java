package com.mpole.hdt.digitaltwin.application.repository.device;

import com.mpole.hdt.digitaltwin.infrastructure.external.mssql.model.ExternalMSViewEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeviceBulkSyncRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void bulkUpsertDevices(List<ExternalMSViewEntity> items, int batchSize) {
        // 1. device_solution 등록 (엑셀의 '장비코드' 기준: AMI, Device_7101 등)
        Set<String> solNames = items.stream()
                .map(ExternalMSViewEntity::getDeviceCode)
                .collect(Collectors.toSet());

        String solSql = "INSERT INTO device_solution (solution_name, created_at, active) " +
                "VALUES (?, now(), true) ON CONFLICT (solution_name) DO NOTHING";

        jdbcTemplate.batchUpdate(solSql, solNames, batchSize, (ps, name) -> ps.setString(1, name));

        // solution_name과 id 매핑 정보 조회 (FK 참조용)
        Map<String, Integer> solMap = jdbcTemplate.query("SELECT solution_name, solution_id FROM device_solution",
                        (rs, rowNum) -> Map.entry(rs.getString("solution_name"), rs.getInt("solution_id")))
                .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 2. device 등록 (엑셀의 '관제점명' 기준: 1F 급수 급탕 등)
        // 동일 솔루션 내에서 관제점명이 같으면 하나의 장비로 간주하여 그룹화
        Map<String, ExternalMSViewEntity> deviceGroup = items.stream()
                .collect(Collectors.toMap(
                        item -> item.getDeviceCode() + ":" + item.getPointName(), // Join Key
                        item -> item,
                        (existing, replacement) -> existing // 중복 시 첫 번째 데이터 유지
                ));

        String devSql = "INSERT INTO device (solution_id, device_name, join_key, created_at, active) " +
                "VALUES (?, ?, ?, now(), true) ON CONFLICT (join_key) DO NOTHING";

        List<String> devKeys = new ArrayList<>(deviceGroup.keySet());
        jdbcTemplate.batchUpdate(devSql, devKeys, batchSize, (ps, key) -> {
            ExternalMSViewEntity item = deviceGroup.get(key);
            ps.setInt(1, solMap.get(item.getDeviceCode()));
            ps.setString(2, item.getPointName()); // 엑셀의 '1F 급수 급탕'
            ps.setString(3, key); // 고유 식별을 위한 join_key (장비코드:관제점명)
        });

        // device join_key와 id 매핑 정보 조회 (FK 참조용)
        Map<String, Integer> devMap = jdbcTemplate.query("SELECT join_key, device_id FROM device",
                        (rs, rowNum) -> Map.entry(rs.getString("join_key"), rs.getInt("device_id")))
                .stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // 3. device_point 등록 (엑셀의 개별 row: C_1F 급수 급탕_ELEC 등)
        String pointSql = "INSERT INTO device_point (device_id, device_code,  point_code, point_name, point_type, unit, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (point_code) DO UPDATE SET " +
                "point_name = EXCLUDED.point_name, point_type = EXCLUDED.point_type";

        jdbcTemplate.batchUpdate(pointSql, items, batchSize, (ps, item) -> {
            String devKey = item.getDeviceCode() + ":" + item.getPointName();
            ps.setInt(1, devMap.get(devKey));
            ps.setString(2, item.getDeviceCode()); // 엑셀의 '관제점코드'
            ps.setString(3, item.getPointCode()); // 엑셀의 '관제점코드'
            ps.setString(4, item.getPointName()); // 엑셀의 '관제점명'
            ps.setString(5, item.getObjectType()); // AI, DI 등
            ps.setString(6, ""); // 필요 시 단위 정보 추가
            ps.setTimestamp(7, Timestamp.valueOf(item.getUpdateDateTime().toLocalDateTime()));
        });
    }

//    @Transactional
//    public void bulkUpsertDevices(List<ExternalMSViewEntity> items, int batchSize) {
//        // solution 을 등록해야함
//        Set<String> solNameSet = new HashSet<>();
//        items.forEach(externalMSViewEntity -> {
//            solNameSet.add(externalMSViewEntity.getDeviceCode());
//        });
//
//        String deviceSolution_sql = """
//                INSERT INTO device_solution (solution_name, created_at, active)
//                VALUES ( ?, ?, true)
//                ON CONFLICT (solution_name)
//                DO NOTHING
//                """;
//
//        jdbcTemplate.batchUpdate(deviceSolution_sql, solNameSet, batchSize, (PreparedStatement ps, String item) -> {
//            ps.setString(1, item);
//            ps.setString(2, OffsetDateTime.now().toString());
//        });
//
//
//
//        // 이건 device 등록 하는것
//        String sql = """
//                INSERT INTO device (join_key, device_name, device_code, point_code, created_at, active)
//                VALUES (?, ?, ?, ?, ?, true)
//                ON CONFLICT (join_key)
//                DO NOTHING
//                """;
//
//        jdbcTemplate.batchUpdate(sql, items, batchSize, (PreparedStatement ps, ExternalMSViewEntity item) -> {
//            String joinKey = item.getDeviceCode() + ":" + item.getPointCode();
//            ps.setString(1, joinKey);
//            ps.setString(2, joinKey);
//            ps.setString(3, item.getDeviceCode());
//            ps.setString(4, item.getPointCode());
//            ps.setTimestamp(5, Timestamp.valueOf(item.getUpdateDateTime().toLocalDateTime()));
//        });
//    }
}
