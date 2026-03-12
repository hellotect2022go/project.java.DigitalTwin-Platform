package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopItemRepository extends JpaRepository<SopItem, Long> {
    List<SopItem> findByStep_Id(Long stepId);
}
