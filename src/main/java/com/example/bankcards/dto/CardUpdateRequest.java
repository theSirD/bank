package com.example.bankcards.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CardUpdateRequest(
        @Min(1) @Max(12) Integer expiryMonth,
        @Min(2020) Integer expiryYear) {
}
