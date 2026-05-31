package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
        Long fromCardId, Long toCardId, BigDecimal amount, Instant timestamp) {
}
