package org.inzight.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inzight.dto.request.AdminCreateUserRequest;
import org.inzight.dto.request.AdminUpdateUserRequest;
import org.inzight.dto.response.UserResponse;
import org.inzight.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    //  Lấy user theo id (cho mục đích khác, vd: xem profile người khác)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    //  Lấy user hiện tại (cho Android gọi sau khi login)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    // --- ADMIN APIs ---

    // Lấy danh sách user (phân trang)
    // URL: /api/users?page=0&size=10&sort=id,desc
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @ParameterObject Pageable pageable // Dùng @ParameterObject nếu dùng springdoc-openapi
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    // Tạo user mới
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid AdminCreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserByAdmin(request));
    }

    // Cập nhật user theo ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUserByAdmin(id, request));
    }

    // Xóa user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
