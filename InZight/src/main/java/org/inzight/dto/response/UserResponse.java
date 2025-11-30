package org.inzight.dto.response;

import lombok.Data;
import org.inzight.enums.RoleName;

import java.time.LocalDate;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private RoleName role;
}