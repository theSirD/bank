package com.example.bankcards.controller;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.service.BlockRequestService;
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
public class AdminBlockRequestController {

    private final BlockRequestService blockRequestService;

    @GetMapping
    public Page<BlockRequestResponse> listPending(Pageable pageable) {
        return blockRequestService.listPending(pageable);
    }

    @PatchMapping("/{id}/approve")
    public BlockRequestResponse approve(@PathVariable Long id) {
        return blockRequestService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public BlockRequestResponse reject(@PathVariable Long id) {
        return blockRequestService.reject(id);
    }
}
