package com.mpole.hdt.digitaltwin.persistence.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Modifying
    @Transactional
    @Query("""
        DELETE FROM UserRole ur 
        WHERE ur.user.userId = :userId 
          AND ur.role.roleId = :roleId
    """)
    void deleteByUserIdAndRoleId(@Param("roleId") Long roleId, @Param("userId") Long userId);
}
