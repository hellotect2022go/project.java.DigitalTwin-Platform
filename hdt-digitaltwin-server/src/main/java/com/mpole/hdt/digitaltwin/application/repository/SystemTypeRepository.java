package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.SystemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemTypeRepository extends JpaRepository<SystemType, Long> {

    /**
     * 시스템 코드로 조회
     */
    Optional<SystemType> findBySysCode(String sysCode);

    /**
     * 시스템 코드 중복 체크
     */
    boolean existsBySysCode(String sysCode);

    /**
     * 활성화된 시스템 타입만 조회
     */
    List<SystemType> findByEnabledTrueOrderBySysCodeAsc();

    /**
     * 검색 (이름 또는 코드)
     */
    @Query("SELECT s FROM SystemType s WHERE " +
           "s.sysNameKo LIKE %:keyword% OR " +
           "s.sysNameEn LIKE %:keyword% OR " +
           "s.sysCode LIKE %:keyword%")
    List<SystemType> searchSystemTypes(@Param("keyword") String keyword);

    /**
     * 전체 시스템 타입 조회 (정렬)
     */
    List<SystemType> findAllByOrderBySysCodeAsc();
}

