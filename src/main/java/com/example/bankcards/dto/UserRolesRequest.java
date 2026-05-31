package com.example.bankcards.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record UserRolesRequest(@NotEmpty Set<String> roles) {
}
