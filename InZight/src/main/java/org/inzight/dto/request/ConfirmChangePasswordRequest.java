package org.inzight.dto.request;


import lombok.Data;

@Data
public class ConfirmChangePasswordRequest {

    private String otp;
    private String oldPassword;
    private String newPassword;
}
