package org.inzight.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.LoginRequest;
import org.inzight.dto.request.RegisterRequest;
import org.inzight.dto.request.VerifyOtpRequest;
import org.inzight.dto.response.AuthResponse;
import org.inzight.dto.response.InitRegisterResponse;
import org.inzight.dto.response.RegisterResponse;
import org.inzight.entity.User;
import org.inzight.enums.RoleName;
import org.inzight.repository.UserRepository;
import org.inzight.security.JwtUtil;
import org.inzight.service.CustomUserDetailsService;
import org.inzight.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final CustomUserDetailsService userDetailsService;

    // ******************************************************
    // CƠ CHẾ LƯU TRỮ TẠM THỜI VÀ MÃ OTP
    private final Map<String, RegisterRequest> tempRegistrationData = new ConcurrentHashMap<>();
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    private String generateOtp() {
        return String.format("%06d", new java.util.Random().nextInt(999999));
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean isValidEmail(String contact) {
        if (contact == null || contact.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(contact).matches();
    }

    private boolean isValidPhone(String contact) {
        return contact != null && contact.matches("[0-9]+") && contact.length() >= 8;
    }
    // ******************************************************



    // ============================
    // 1) INIT REGISTER + SEND OTP
    // ============================
    @PostMapping("/init-register")
    public ResponseEntity<?> initRegister(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username đã tồn tại!");
        }
        if (request.email() != null && !request.email().isEmpty()) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                return ResponseEntity.badRequest().body("Email đã tồn tại!");
            }
        }

        if (!isValidEmail(request.email())) {
            return ResponseEntity.badRequest().body("Vui lòng cung cấp Email hợp lệ để gửi mã OTP.");
        }

        String token = UUID.randomUUID().toString();
        String otpCode = generateOtp();

        tempRegistrationData.put(token, request);
        otpStorage.put(token, otpCode);

        emailService.sendOtpEmail(request.email(), otpCode);

        return ResponseEntity.ok(new InitRegisterResponse(token));
    }



    // ============================
    // 2) VERIFY OTP + CREATE USER
    // ============================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

        String token = request.registrationToken();
        String otpSent = request.otp();

        if (!tempRegistrationData.containsKey(token) || !otpStorage.containsKey(token)) {
            return ResponseEntity.badRequest().body("Token đăng ký không hợp lệ hoặc đã hết hạn.");
        }

        if (!otpStorage.get(token).equals(otpSent)) {
            return ResponseEntity.badRequest().body("Mã OTP không chính xác.");
        }

        RegisterRequest registerRequest = tempRegistrationData.get(token);

        LocalDate dob = null;
        try {
            if (registerRequest.dateOfBirth() != null && !registerRequest.dateOfBirth().isEmpty()) {
                dob = LocalDate.parse(registerRequest.dateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (Exception ignored) {}

        User user = User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .fullName(registerRequest.fullName())
                .password(passwordEncoder.encode(registerRequest.password()))
                .dateOfBirth(dob)
                .gender(registerRequest.gender())
                .role(RoleName.valueOf("USER"))
                .build();

        userRepository.save(user);

        tempRegistrationData.remove(token);
        otpStorage.remove(token);

        // 🔥 SỬ DỤNG generateToken(User user)
        String jwtToken = jwtUtil.generateToken(user);

        RegisterResponse authResponse = new RegisterResponse(
                jwtToken,
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getFullName(),
                user.getRole()
        );

        return ResponseEntity.ok(authResponse);
    }



    // ============================
    // 3) LOGIN
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.contact(), request.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Lấy user từ DB
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 🔥 DÙNG generateToken(User user) — KHÔNG dùng UserDetails
            String token = jwtUtil.generateToken(user);

            AuthResponse authResponse = new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getRole()
            );

            return ResponseEntity.ok(authResponse);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Sai username hoặc password!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi server: " + e.getMessage());
        }
    }

}
