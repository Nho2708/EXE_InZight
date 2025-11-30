package org.inzight.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.inzight.enums.RoleName;

import java.time.LocalDate;

@Getter
@Setter
public class AdminUpdateUserRequest {
    private String fullName;
    private String phone;
    private RoleName role; // Admin có thể đổi Role
    private String gender;
    private LocalDate dateOfBirth;
    // Admin thường không đổi password ở đây, hoặc dùng API reset password riêng
    // Email/Username nếu cho đổi thì cần check trùng lặp kỹ
    private Boolean isActive; // Nếu có field này để khóa tài khoản
}