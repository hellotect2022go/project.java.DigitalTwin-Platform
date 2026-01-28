package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.DeviceSystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceSystemTypeRepository extends JpaRepository<DeviceSystemType, Long> {

    Optional<DeviceSystemType> findBySysCode(String sysCode);

    List<DeviceSystemType> findByEnabledTrueOrderBySysCode();

    boolean existsBySysCode(String sysCode);
}
