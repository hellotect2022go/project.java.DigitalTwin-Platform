package com.mpole.hdt.digitaltwin.application.repository.sop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SopItemOptionRepository extends JpaRepository<SopItemOption, Long> {
}
