package com.mpole.hdt.digitaltwin.infrastructure.external.mssql.repository;

import com.mpole.hdt.digitaltwin.infrastructure.external.mssql.model.ExternalMSViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MssqlRepository extends JpaRepository<ExternalMSViewEntity, String> {

    //List<ExternalMSViewEntity>
}
