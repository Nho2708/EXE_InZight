package org.inzight.controller;


import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.UserRegisterRequest;
import org.inzight.dto.response.UserResponse;
import org.inzight.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}