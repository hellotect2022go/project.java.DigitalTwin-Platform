package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopRuleActionRepository extends JpaRepository<SopRuleAction, Long> {
    List<SopRuleAction> findByRule_Id(Long ruleId);
}
