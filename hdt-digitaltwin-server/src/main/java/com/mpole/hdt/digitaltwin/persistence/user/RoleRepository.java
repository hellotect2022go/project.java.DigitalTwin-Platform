package com.mpole.hdt.digitaltwin.persistence.user;

import com.mpole.hdt.digitaltwin.api.dto.user.RoleCountPrj;
import com.mpole.hdt.digitaltwin.api.dto.user.UserInRolePrj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
    // ID 기준 오름차순 정렬
    List<Role> findAllByOrderByRoleIdAsc();


    @Query("""
            SELECT
             r.roleId as roleId,
             r.description as description,
             r.roleName as roleName,
             COUNT(ur.user) as userCount
         FROM Role r
         LEFT JOIN UserRole ur ON ur.role = r
         GROUP BY r.roleId, r.description, r.roleName
         ORDER BY r.roleId ASC
            """)
    List<RoleCountPrj> findRoleInfosWithUserCount();

    @Query("""
            SELECT
             u.userId as userId,
             u.loginId as loginId,
             u.username as username
         FROM User u
         LEFT JOIN UserRole ur ON ur.user = u
         WHERE ur.role.roleId = :roleId
         """)
    List<UserInRolePrj> findUsersInRole(@Param("roleId") Long roleId);

    @Query("""
        SELECT
             u.userId as userId,
             u.loginId as loginId,
             u.username as username
        FROM User u
        WHERE u.userId NOT IN (
           SELECT ur.user.userId
           FROM UserRole ur
           WHERE ur.role.roleId = :roleId
        )
        AND (
              :searchKey IS NULL OR :searchKey = '' OR
              u.username LIKE CONCAT('%', :searchKey, '%') OR
              u.loginId LIKE CONCAT('%', :searchKey, '%')
          )
        """)
    List<UserInRolePrj> findUsersExceptRole(@Param("roleId") Long roleId, @Param("searchKey") String searchKey);
}
