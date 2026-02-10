package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.SopRuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopRuleConditionRepository extends JpaRepository<SopRuleCondition, Long> {
    List<SopRuleCondition> findByRule_Id(Long ruleId);
}
