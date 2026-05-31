package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;

public record UserEnableRequest(@NotNull Boolean enabled) {
}
