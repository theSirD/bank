package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    @GetMapping
    public Page<CardResponse> listMyCards(
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return cardService.listMyCards(status, search, pageable);
    }

    @GetMapping("/{id}")
    public CardResponse getMyCard(@PathVariable Long id) {
        return cardService.getMyCard(id);
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id) {
        return cardService.getBalance(id);
    }

    @PostMapping("/{id}/block-request")
    public BlockRequestResponse requestBlock(@PathVariable Long id) {
        return blockRequestService.requestBlock(id);
    }
}
