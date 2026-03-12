package com.mpole.hdt.digitaltwin.application.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocZoneRepository extends JpaRepository<LocZone,Long> {

}
