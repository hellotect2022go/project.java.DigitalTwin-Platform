package com.mpole.hdt.digitaltwin.application.repository.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SopStepStatusRepository extends JpaRepository<SopStepStatus, Long> {
    Optional<SopStepStatus> findByInstance_IdAndStep_Id(Long instanceId, Long stepId);
    List<SopStepStatus> findByInstance_Id(Long instanceId);
}
