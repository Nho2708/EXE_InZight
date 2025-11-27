package org.inzight.service;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChangeEmailRequest;
import org.inzight.dto.request.ChangePasswordRequest;
import org.inzight.dto.request.ConfirmEmailChangeRequest;
import org.inzight.dto.response.UserResponse;
import org.inzight.entity.User;
import org.inzight.repository.UserRepository;
import org.inzight.security.AuthUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

import org.inzight.dto.request.ConfirmChangePasswordRequest;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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




}
