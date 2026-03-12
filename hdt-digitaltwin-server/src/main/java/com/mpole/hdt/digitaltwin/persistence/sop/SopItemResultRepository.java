package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SopItemResultRepository extends JpaRepository<SopItemResult, Long> {

    Optional<SopItemResult> findByInstance_IdAndItem_Id(Long instanceId, Long itemId);

    List<SopItemResult> findByInstance_Id(Long instanceId);
}
