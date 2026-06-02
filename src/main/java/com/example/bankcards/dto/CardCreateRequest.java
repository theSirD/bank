package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CardCreateRequest(
        @NotNull Long ownerId,
        @NotBlank @Pattern(regexp = "\\d{16}") String pan,
        @Min(1) @Max(12) int expiryMonth,
        @Min(2020) int expiryYear,
        @JsonAlias("balance")
        @DecimalMin("0.00") BigDecimal initialBalance) {
}
