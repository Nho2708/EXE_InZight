package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.ChangeEmailRequest;
import org.inzight.dto.request.ChangePasswordRequest;
import org.inzight.dto.request.ConfirmChangePasswordRequest;
import org.inzight.dto.request.ConfirmEmailChangeRequest;
import org.inzight.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final UserService userService;

    @PostMapping("/password/request-otp")
    public ResponseEntity<?> requestPasswordOtp(@RequestBody ChangePasswordRequest req) {
        return ResponseEntity.ok(userService.requestChangePassword(req));
    }

    @PostMapping("/password/confirm")
    public ResponseEntity<?> confirmPasswordChange(@RequestBody ConfirmChangePasswordRequest req) {
        return ResponseEntity.ok(userService.confirmChangePassword(req));
    }

    @PostMapping("/email/request-otp")
    public ResponseEntity<?> requestEmailOtp(@RequestBody ChangeEmailRequest req) {
        return ResponseEntity.ok(userService.requestChangeEmail(req));
    }

    @PostMapping("/email/confirm")
    public ResponseEntity<?> confirmEmailChange(@RequestBody ConfirmEmailChangeRequest req) {
        return ResponseEntity.ok(userService.confirmChangeEmail(req));
    }
}
