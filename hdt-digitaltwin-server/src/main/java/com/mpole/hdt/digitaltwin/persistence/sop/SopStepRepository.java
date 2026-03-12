package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopStepRepository extends JpaRepository<SopStep, Long> {
    List<SopStep> findByTemplate_IdOrderByStepOrderAsc(Long templateId);
}
