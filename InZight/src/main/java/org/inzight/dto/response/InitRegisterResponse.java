package org.inzight.dto.response;

// DTO phản hồi sau khi user gửi yêu cầu Init Register thành công
public record InitRegisterResponse(String registrationToken) {}