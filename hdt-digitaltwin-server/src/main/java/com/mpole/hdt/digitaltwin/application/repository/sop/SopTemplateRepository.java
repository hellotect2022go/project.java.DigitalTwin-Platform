package com.mpole.hdt.digitaltwin.application.repository.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopTemplateRepository extends JpaRepository<SopTemplate, Long> {

    List<SopTemplate> findByEventType(String eventType);

    List<SopTemplate> findByEventTypeAndIsActiveTrue(String eventType);

    List<SopTemplate> findByIsActiveTrue();
}
