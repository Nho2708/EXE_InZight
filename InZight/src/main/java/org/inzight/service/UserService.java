package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.*;
import org.inzight.dto.response.UserResponse;
import org.inzight.entity.User;
import org.inzight.entity.Wallet;
import org.inzight.enums.RoleName;
import org.inzight.repository.UserRepository;
import org.inzight.repository.WalletRepository;
import org.inzight.security.AuthUtil;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AuthUtil authUtil;

    private final PasswordEncoder passwordEncoder;

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailSettingService emailsettingService;




    private static final long OTP_TTL_SECONDS = 300;


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

        System.out.println("🔍 UserService.getCurrentUser - User ID: " + currentUserId);
        System.out.println("🔍 UserService.getCurrentUser - User rank from DB: " + user.getRank());
        
        UserResponse response = mapToResponse(user);
        System.out.println("🔍 UserService.getCurrentUser - Response rank: " + response.getRank());
        
        return response;
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
        res.setRole(user.getRole());
        
        // Map rank từ User entity
        String rank = user.getRank() != null ? user.getRank() : "FREE";
        res.setRank(rank);
        
        // Log để debug
        System.out.println("🔍 UserService.mapToResponse - User ID: " + user.getId() + ", Username: " + user.getUsername() + ", Rank: " + rank);
        
        return res;
    }

    public String requestChangePassword(ChangePasswordRequest req) {

        User currentUser = authUtil.getCurrentUser();


        if (!currentUser.getEmail().equals(req.getEmail())) {
            throw new RuntimeException("You are not allowed to request OTP for another email");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        String key = "otp:" + currentUser.getEmail();
        redisTemplate.opsForValue().set(key, otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);

        emailsettingService.send(
                currentUser.getEmail(),
                "Your OTP Code to Change Password",
                "Your verification code is: " + otp + "\nIt expires in 5 minutes."
        );

        return "OTP sent to your email";
    }

    public String confirmChangePassword(ConfirmChangePasswordRequest req) {

        User currentUser = authUtil.getCurrentUser();


        if (!passwordEncoder.matches(req.getOldPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }


        String key = "otp:" + currentUser.getEmail();
        String savedOtp = redisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new RuntimeException("OTP expired or not found");
        }

        if (!savedOtp.equals(req.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }


        if (passwordEncoder.matches(req.getNewPassword(), currentUser.getPassword())) {
            throw new RuntimeException("New password must be different from the old password");
        }
        currentUser.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(currentUser);
        redisTemplate.delete(key);
        return "Password changed successfully";
    }

    public String requestChangeEmail(ChangeEmailRequest req) {

        User currentUser = authUtil.getCurrentUser();
        String oldEmail = currentUser.getEmail();


        if (oldEmail.equals(req.getNewEmail())) {
            throw new RuntimeException("The new email must be different from the current email.");
        }


        if (userRepository.existsByEmail(req.getNewEmail())) {
            throw new RuntimeException("New email has been used.");
        }


        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));


        String key = "otp:change-email:" + currentUser.getId();
        redisTemplate.opsForValue().set(key, otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);


        emailsettingService.send(
                oldEmail,
                "OTP confirmation email change",
                "The OTP code to change your email is: " + otp + "\n" + "Code expires after 5 minutes."
        );

        return "OTP has been sent to the old email.";
    }

    public String confirmChangeEmail(ConfirmEmailChangeRequest req) {

        User currentUser = authUtil.getCurrentUser();


        if (!currentUser.getEmail().equals(req.getOldEmail())) {
            throw new RuntimeException("The old email is incorrect. Please check again.");
        }


        if (!passwordEncoder.matches(req.getPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Password is incorrect.");
        }


        String key = "otp:change-email:" + currentUser.getId();
        String savedOtp = redisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new RuntimeException("OTP has expired or does not exist.");
        }

        if (!savedOtp.equals(req.getOtp())) {
            throw new RuntimeException("OTP is incorrect.");
        }


        if (userRepository.existsByEmail(req.getNewEmail())) {
            throw new RuntimeException("New email already used.");
        }


        if (req.getOldEmail().equals(req.getNewEmail())) {
            throw new RuntimeException("The new email must be different from the old email.");
        }

        currentUser.setEmail(req.getNewEmail());
        userRepository.save(currentUser);


        redisTemplate.delete(key);

        return "Email changed successfully";
    }

    // --- ADMIN SECTION ---

    // 1. Get All Users (Có phân trang)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // 2. Create User (By Admin)
    public UserResponse createUserByAdmin(AdminCreateUserRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .role(req.getRole() != null ? req.getRole() : RoleName.USER) // Default USER nếu null
                .gender(req.getGender())
                .dateOfBirth(req.getDateOfBirth())
                .build();

        User savedUser = userRepository.save(user);
        
        // Tự động tạo wallet "Transfer" cho user mới
        Wallet defaultWallet = Wallet.builder()
                .user(savedUser)
                .name("Transfer")
                .balance(BigDecimal.ZERO)
                .currency("VND")
                .build();
        walletRepository.save(defaultWallet);
        
        return mapToResponse(savedUser);
    }

    // 3. Update User (By Admin)
    public UserResponse updateUserByAdmin(Long id, AdminUpdateUserRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật các trường thông tin
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getGender() != null) user.setGender(req.getGender());
        if (req.getDateOfBirth() != null) user.setDateOfBirth(req.getDateOfBirth());

        // Cập nhật Role nếu có
        if (req.getRole() != null) {
            // Có thể thêm check không cho phép Admin tự hạ quyền chính mình nếu cần
            user.setRole(req.getRole());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }

    // 4. Delete User
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Prevent deleting yourself (optional safety check)
        Long currentUserId = authUtil.getCurrentUserId();
        if (user.getId().equals(currentUserId)) {
            throw new RuntimeException("You cannot delete your own account.");
        }

        userRepository.delete(user);
    }


}
