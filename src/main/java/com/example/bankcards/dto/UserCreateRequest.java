package com.example.bankcards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UserCreateRequest(
        @NotBlank @Size(max = 100) String username,
        @Email String email,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotEmpty Set<String> roles) {
}
