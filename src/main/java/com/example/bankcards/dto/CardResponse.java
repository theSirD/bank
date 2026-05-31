package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;

public record CardResponse(
        Long id,
        String maskedPan,
        String ownerUsername,
        int expiryMonth,
        int expiryYear,
        CardStatus status,
        BigDecimal balance) {
}
