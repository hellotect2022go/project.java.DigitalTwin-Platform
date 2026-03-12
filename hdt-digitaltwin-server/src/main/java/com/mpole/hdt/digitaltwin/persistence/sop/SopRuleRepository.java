package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopRuleRepository extends JpaRepository<SopRule, Long> {
    List<SopRule> findByRuleType(String ruleType);
    List<SopRule> findByTemplate_Id(Long templateId);
    List<SopRule> findByTemplate_IdAndRuleType(Long templateId, String ruleType);
}
