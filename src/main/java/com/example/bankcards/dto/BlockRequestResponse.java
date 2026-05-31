package com.example.bankcards.dto;

import com.example.bankcards.entity.BlockRequestStatus;
import java.time.Instant;

public record BlockRequestResponse(
        Long id,
        Long cardId,
        String ownerUsername,
        String maskedPan,
        BlockRequestStatus status,
        Instant createdAt,
        Instant resolvedAt) {
}
