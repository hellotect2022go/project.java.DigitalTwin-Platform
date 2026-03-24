package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.auth.RoleRequest;
import com.mpole.hdt.digitaltwin.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UserRoleService userRoleService;

    /**
     * 권한(부서?) 관련 API
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getRoleInfos() {
        return ResponseEntity.ok(ApiResponse.success("권한 목록 조회",userRoleService.getRoleInfos()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createRoleInfos(@RequestBody RoleRequest request) {
        userRoleService.createRoleInfos(request);
        return ResponseEntity.ok(ApiResponse.success("권한 목록 생성",userRoleService.getRoleInfos()));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse> updateRoleInfos(@PathVariable("roleId") Long roleId,@RequestBody RoleRequest request) {
        userRoleService.updateRoleInfos(roleId, request);
        return ResponseEntity.ok(ApiResponse.success("권한 목록 수정"));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse> deleteRoleInfos(@PathVariable("roleId") Long roleId) {
        userRoleService.deleteRoleInfos(roleId);
        return ResponseEntity.ok(ApiResponse.success("권한 목록 삭제"));
    }

    @GetMapping("/{roleId}/users")
    public ResponseEntity<ApiResponse> getUsersInRole(@PathVariable("roleId") Long roleId) {
        return ResponseEntity.ok(ApiResponse.success("권한별 소속 사용자 목록",userRoleService.getUsersInRole(roleId)));
    }

    // 특정 roleId 를 제외한 나머지 사람들 목록 조회
    @GetMapping("/{roleId}/users/exclude")
    public ResponseEntity<ApiResponse> getUsersExceptRole(
            @PathVariable("roleId") Long roleId,
            @RequestParam(value = "search", required = false)String searchKey) {
        return ResponseEntity.ok(ApiResponse.success("권한별 소속 사용자 목록",userRoleService.getUsersExceptRole(roleId, searchKey)));
    }

    // 권한에 사용자 매핑 추가
    @PostMapping("/{roleId}/users/{userId}")
    public ResponseEntity<ApiResponse> addUserToRole(
            @PathVariable("roleId") Long roleId,
            @PathVariable("userId") Long userId ) {

        userRoleService.addUserToRole(roleId, userId);
        return ResponseEntity.ok(ApiResponse.success("권한에 사용자 매핑 추가"));
    }

    // 권한에 사용자 매핑 제거
    @DeleteMapping("/{roleId}/users/{userId}")
    public ResponseEntity<ApiResponse> removeUserFromRole(
            @PathVariable("roleId") Long roleId,
            @PathVariable("userId") Long userId ) {

        userRoleService.removeUserFromRole(roleId, userId);
        return ResponseEntity.ok(ApiResponse.success("권한에서 사용자 매핑 제거"));
    }



}
