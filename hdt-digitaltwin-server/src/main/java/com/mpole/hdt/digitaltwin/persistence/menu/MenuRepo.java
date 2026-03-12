package com.mpole.hdt.digitaltwin.persistence.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepo extends JpaRepository<Menu,Long> {

    @Query("SELECT m from Menu m LEFT JOIN FETCH m.parentMenu ORDER BY m.depth, m.sortOrder ")
    List<Menu> findAllMenus();


    @Query("""
            SELECT m FROM Menu m 
            JOIN m.roleMenus rm 
            JOIN rm.role r 
            WHERE r.roleName IN :roles
            """)
    List<Menu> findMenusByRole(@Param("roles")List<String> roles);
}
