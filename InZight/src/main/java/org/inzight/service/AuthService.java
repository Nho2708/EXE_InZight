package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.LoginRequest;
import org.inzight.dto.request.RegisterRequest;
import org.inzight.dto.response.AuthResponse;
import org.inzight.dto.response.RegisterResponse;
import org.inzight.entity.User;
import org.inzight.enums.RoleName;
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
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .fullName(request.fullName())
                .password(passwordEncoder.encode(request.password()))
                .avatarUrl(null)
                .role(RoleName.valueOf("USER"))            // 👈 GÁN QUYỀN MẶC ĐỊNH
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(user);

        return new RegisterResponse(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFullName(),
                user.getRole()
        );
    }

    // -------------------- LOGIN -------------------- //
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.contact())
                .or(() -> userRepository.findByEmail(request.contact()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
}
