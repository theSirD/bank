package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreateRequest;
import com.example.bankcards.dto.UserEnableRequest;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.dto.UserRolesRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "admin-user-controller", description = "Административное управление пользователями")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя с заданными ролями и параметрами доступа.")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Список пользователей", description = "Возвращает постраничный список пользователей для администратора.")
    public Page<UserResponse> listUsers(@Parameter(hidden = true) Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Включить или отключить пользователя", description = "Изменяет флаг enabled у выбранного пользователя.")
    public UserResponse setEnabled(@PathVariable Long id, @Valid @RequestBody UserEnableRequest request) {
        return userService.setEnabled(id, request.enabled());
    }

    @PatchMapping("/{id}/roles")
    @Operation(summary = "Изменить роли пользователя", description = "Обновляет набор ролей (например ROLE_USER/ROLE_ADMIN) для выбранного пользователя.")
    public UserResponse assignRoles(@PathVariable Long id, @Valid @RequestBody UserRolesRequest request) {
        return userService.assignRoles(id, request.roles());
    }
}
