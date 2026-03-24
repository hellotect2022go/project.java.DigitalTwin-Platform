package com.mpole.hdt.digitaltwin.persistence.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {

//    @Query("""
//            SELECT rm FROM RoleMenu rm
//            JOIN rm.role r
//            LEFT JOIN FETCH rm.menu
//            WHERE r.roleName = :roleName
//            """)
//    List<RoleMenu> findMenuByRole(@Param("roleName")String roleName);
}
