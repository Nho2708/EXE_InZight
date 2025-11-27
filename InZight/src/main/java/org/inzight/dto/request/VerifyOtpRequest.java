package org.inzight.dto.request;

public record VerifyOtpRequest(String registrationToken, String otp) {}