package org.inzight.service.impl;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.UserRegisterRequest;
import org.inzight.dto.response.UserResponse;
import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import org.inzight.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse register(UserRegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // TODO: hash password
                .fullName(request.getFullName())
                .build();
        userRepository.save(user);

        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        return res;
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setAvatarUrl(user.getAvatarUrl());
        return res;
    }
}
