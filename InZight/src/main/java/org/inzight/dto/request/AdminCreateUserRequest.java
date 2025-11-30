package org.inzight.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.inzight.enums.RoleName;

import java.time.LocalDate;

@Getter
@Setter
public class AdminCreateUserRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String fullName;
    private String phone;
    private RoleName role; // Admin có thể chọn Role khi tạo
    private String gender;
    private LocalDate dateOfBirth;
}