package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.service.BlockRequestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/block-requests")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "admin-block-request-controller", description = "Обработка заявок на блокировку карт")
public class AdminBlockRequestController {

    private final BlockRequestService blockRequestService;

    @GetMapping
    @PageableAsQueryParam
    @Operation(summary = "Список заявок на блокировку", description = "Возвращает постраничный список заявок на блокировку карт.")
    public Page<BlockRequestResponse> listPending(@Parameter(hidden = true) Pageable pageable) {
        return blockRequestService.listPending(pageable);
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Одобрить заявку", description = "Одобряет заявку на блокировку и переводит карту в статус BLOCKED.")
    public BlockRequestResponse approve(@PathVariable Long id) {
        return blockRequestService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Отклонить заявку", description = "Отклоняет заявку на блокировку и возвращает карте допустимый статус.")
    public BlockRequestResponse reject(@PathVariable Long id) {
        return blockRequestService.reject(id);
    }
}
