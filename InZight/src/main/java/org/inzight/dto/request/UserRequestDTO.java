package org.inzight.dto.request;


import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String fullName;
}