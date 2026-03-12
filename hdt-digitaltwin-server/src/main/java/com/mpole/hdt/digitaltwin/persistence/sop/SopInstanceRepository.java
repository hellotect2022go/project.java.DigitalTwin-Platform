package com.mpole.hdt.digitaltwin.persistence.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SopInstanceRepository extends JpaRepository<SopInstance, Long> {
}
