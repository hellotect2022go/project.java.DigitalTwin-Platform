package com.mpole.hdt.digitaltwin.service;

import com.mpole.hdt.digitaltwin.api.dto.auth.RoleRequest;
import com.mpole.hdt.digitaltwin.persistence.user.*;
import com.mpole.hdt.digitaltwin.api.dto.user.RoleCountPrj;
import com.mpole.hdt.digitaltwin.api.dto.user.UserInRolePrj;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;


    public List<RoleCountPrj> getRoleInfos() {
        //List<RoleRequest> result = roleRepo.findAllByOrderByRoleIdAsc().stream().map(RoleRequest::toDto).toList();
        List<RoleCountPrj> result = roleRepository.findRoleInfosWithUserCount();
        return result;
    }

    @Transactional
    public void createRoleInfos(RoleRequest roleDTO) {
        Role role = Role.builder()
                .roleName(roleDTO.roleName())
                .description(roleDTO.description())
                .build();
        roleRepository.save(role);
    }

    @Transactional
    public void updateRoleInfos(Long roleId, RoleRequest roleDTO) {
        Role role = roleRepository.findById(roleId).get();
        role.setRoleName(roleDTO.roleName());
        role.setDescription(roleDTO.description());
    }


    @Transactional
    public void deleteRoleInfos(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    @Transactional
    public List<UserInRolePrj> getUsersInRole(Long roleId) {
        return roleRepository.findUsersInRole(roleId);
    }

    @Transactional
    public List<UserInRolePrj> getUsersExceptRole(Long roleId, String searchKey) {
        return roleRepository.findUsersExceptRole(roleId, searchKey);
    }

    @Transactional
    public void addUserToRole(Long roleId, Long userId) {
        User user = userRepository.getReferenceById(userId); // DB 안 갔다옴
        Role role = roleRepository.getReferenceById(roleId); // DB 안 갔다옴

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeUserFromRole(Long roleId, Long userId) {
        userRoleRepository.deleteByUserIdAndRoleId(roleId, userId);
    }
}
