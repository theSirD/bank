package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CardUpdateRequest(
        @Min(1) @Max(12) Integer expiryMonth,
        @Min(2020) Integer expiryYear) {

    @AssertTrue(message = "At least one field must be provided for update")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isAnyFieldProvided() {
        return expiryMonth != null || expiryYear != null;
    }
}
