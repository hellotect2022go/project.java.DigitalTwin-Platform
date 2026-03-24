package com.mpole.hdt.digitaltwin.persistence.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {

    Boolean existsByCategoryId(Long categoryId);

    @Modifying
    @Query("UPDATE Menu m set m.sortOrder = :sortOrder WHERE m.menuId = :menuId")
    void updateMenuSortOrder(@Param("menuId") Long menuId, @Param("sortOrder")Integer sortOrder);


    @Query("SELECT m from Menu m ORDER BY m.sortOrder ")
    List<Menu> findAllMenus();


    @Query("""
            SELECT m FROM Menu m 
            JOIN m.roleMenus rm 
            JOIN rm.role r 
            WHERE r.roleName IN :roles
            """)
    List<Menu> findMenusByRole(@Param("roles")List<String> roles);
}
