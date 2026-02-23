package com.mpole.hdt.digitaltwin.application.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuRepo extends JpaRepository<RoleMenu, Long> {

//    @Query("""
//            SELECT rm FROM RoleMenu rm
//            JOIN rm.role r
//            LEFT JOIN FETCH rm.menu
//            WHERE r.roleName = :roleName
//            """)
//    List<RoleMenu> findMenuByRole(@Param("roleName")String roleName);
}
