package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    
    /**
     * Device ID로 조회
     */
    Optional<Device> findByDeviceId(String deviceId);
    
    /**
     * Device ID 존재 여부
     */
    boolean existsByDeviceId(String deviceId);
    
    /**
     * DeviceModel별 조회
     */
    List<Device> findByDeviceModel_Id(Long deviceModelId);
    
    /**
     * 활성화된 장비만 조회
     */
    List<Device> findByEnabledTrue();
    
    /**
     * 층별 조회
     */
    List<Device> findByFloor(String floor);
    
    /**
     * 구역별 조회
     */
    List<Device> findByZone(String zone);
    
    /**
     * 층 + 구역별 조회
     */
    List<Device> findByFloorAndZone(String floor, String zone);
    
    /**
     * 상태별 조회
     */
    List<Device> findByStatus(String status);
    
    /**
     * 온라인 장비 조회
     */
    List<Device> findByOnlineTrue();
    
    /**
     * 층별 활성화된 장비 조회
     */
    List<Device> findByFloorAndEnabledTrue(String floor);
    
    /**
     * 검색 (Device ID, Name, Location)
     */
    @Query("SELECT d FROM Device d WHERE " +
           "d.deviceId LIKE %:keyword% OR " +
           "d.deviceName LIKE %:keyword% OR " +
           "d.location LIKE %:keyword%")
    List<Device> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * DeviceModel + 상태별 조회
     */
    List<Device> findByDeviceModel_IdAndStatus(Long deviceModelId, String status);
    
    /**
     * 카테고리별 장비 조회 (DeviceModel을 통해)
     */
    @Query("SELECT d FROM Device d WHERE d.deviceModel.category.id = :categoryId")
    List<Device> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 시스템 타입별 장비 조회
     */
    @Query("SELECT d FROM Device d WHERE d.deviceModel.systemType.id = :systemTypeId")
    List<Device> findBySystemTypeId(@Param("systemTypeId") Long systemTypeId);
}

