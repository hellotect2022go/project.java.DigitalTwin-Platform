package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.SopRuleAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopRuleActionRepository extends JpaRepository<SopRuleAction, Long> {
    List<SopRuleAction> findByRule_Id(Long ruleId);
}
