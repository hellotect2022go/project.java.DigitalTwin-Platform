package com.mpole.hdt.digitaltwin.application.repository;

import com.mpole.hdt.digitaltwin.application.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByLoginId(String loginId);
    
//    Optional<User> findByUsername(String username);
//
//    boolean existsByLoginId(String loginId);
//
//    boolean existsByUsername(String username);
//
//    boolean existsByEmail(String email);
}

