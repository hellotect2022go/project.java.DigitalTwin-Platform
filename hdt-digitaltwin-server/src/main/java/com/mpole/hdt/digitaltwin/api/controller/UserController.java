package com.mpole.hdt.digitaltwin.api.controller;

import com.mpole.hdt.digitaltwin.api.dto.ApiResponse;
import com.mpole.hdt.digitaltwin.api.dto.user.UserCreateRequest;
import com.mpole.hdt.digitaltwin.api.dto.user.UserUpdateRequest;
import com.mpole.hdt.digitaltwin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자
     */
    @GetMapping
    public ResponseEntity<ApiResponse> fetchUsers(
            @RequestParam(value = "active", required = false)Boolean active,
            @RequestParam(value = "loginId", required = false)String loginId,
            @RequestParam(value = "username", required = false)String username,
            @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success("사용자 목록 조회", userService.getUsers(active, loginId,username, pageable)));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse> createUsers(@RequestBody UserCreateRequest request) {
        userService.createUsers(request);
        return ResponseEntity.ok(ApiResponse.success("사용자 업데이트"));
    }


    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUsers(@PathVariable("userId") Long userId,
                                                   @RequestBody UserUpdateRequest request) {
        userService.updateUsers(userId, request);
        return ResponseEntity.ok(ApiResponse.success("사용자 업데이트"));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUsers(@PathVariable("userId") Long userId) {
        userService.deleteUsers(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 삭제"));
    }
}
