package org.inzight.dto.response;

public record PaymentResponse(
        String checkoutUrl,
        Long orderCode
) {}

