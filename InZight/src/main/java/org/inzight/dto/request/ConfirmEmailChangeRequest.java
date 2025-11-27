package org.inzight.dto.request;

import lombok.Data;

@Data
public class ConfirmEmailChangeRequest {
    private String oldEmail;
    private String newEmail;
    private String otp;
    private String password;
}
