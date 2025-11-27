package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.LoginRequest;
import org.inzight.dto.request.RegisterRequest;
import org.inzight.dto.response.AuthResponse;
import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import org.inzight.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // -------------------- REGISTER -------------------- //
    public AuthResponse register(RegisterRequest request) {

        // Check trùng username
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Check trùng email
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Tạo user mới
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl(null)
                .build();

        userRepository.save(user);

        // Load userDetails cho JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFullName()
        );
    }

    // -------------------- LOGIN -------------------- //
    public AuthResponse login(LoginRequest request) {

        // Cho phép login bằng username / email / phone
        User user = userRepository.findByUsername(request.contact())
                .or(() -> userRepository.findByEmail(request.contact()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFullName()
        );
    }
}
