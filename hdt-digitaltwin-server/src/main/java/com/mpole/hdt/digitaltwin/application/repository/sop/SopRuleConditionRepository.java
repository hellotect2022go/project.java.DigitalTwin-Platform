package com.mpole.hdt.digitaltwin.application.repository.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopRuleConditionRepository extends JpaRepository<SopRuleCondition, Long> {
    List<SopRuleCondition> findByRule_Id(Long ruleId);
}
