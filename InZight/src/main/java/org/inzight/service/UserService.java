package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.response.UserResponse;
import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    // Lấy user theo ID (dùng cho /users/{id})
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    // Lấy thông tin user hiện tại (dùng cho /users/me)
    public UserResponse getCurrentUser() {
        Long currentUserId = authUtil.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    //  Helper để map entity → DTO
    private UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setPhone(user.getPhone());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setGender(user.getGender());
        return res;
    }
}
