package com.mpole.hdt.digitaltwin.service;

import com.mpole.hdt.digitaltwin.api.dto.user.UserCreateRequest;
import com.mpole.hdt.digitaltwin.persistence.user.User;
import com.mpole.hdt.digitaltwin.persistence.user.UserRepository;
import com.mpole.hdt.digitaltwin.api.dto.user.UserUpdateRequest;
import com.mpole.hdt.digitaltwin.api.dto.user.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> getUsers(Boolean active, String loginId, String username, Pageable pageable) {
        Page<UserResponse> list = userRepository.findUsersByCondition(active,loginId,username,pageable).map(UserResponse::from);
        return list;
    }

    @Transactional
    public void createUsers(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByLoginId(userCreateRequest.loginId())) {
            throw new IllegalStateException("이미 존재하는 ID 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
        User user = User.create(userCreateRequest, encodedPassword);
        userRepository.save(user);
    }

    @Transactional
    public void updateUsers(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자가 없습니다."));

        String encodedPassword = null;
        if (StringUtils.hasText(userUpdateRequest.password())) {
            encodedPassword = passwordEncoder.encode(userUpdateRequest.password());
        }

        user.updateInfo(userUpdateRequest, encodedPassword);
    }

    @Transactional
    public void deleteUsers(Long userId) {
        userRepository.deleteById(userId);
    }

}
