package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.BlockRequestService;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
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
@Tag(name = "user-card-controller", description = "Операции пользователя со своими картами")
public class UserCardController {

    private final CardService cardService;
    private final BlockRequestService blockRequestService;

    @GetMapping
    @PageableAsQueryParam
    @Operation(
            summary = "Список моих карт",
            description = "Возвращает постраничный список карт текущего пользователя. "
                    + "search ищет по последним 4 цифрам карты (panLast4), поддерживается частичное совпадение. "
                    + "Если передан search, он имеет приоритет над фильтром status.")
    public Page<CardResponse> listMyCards(
            @RequestParam(required = false) CardStatus status,
            @Parameter(description = "Поиск по последним 4 цифрам карты (panLast4), например 1111 или 11")
            @RequestParam(required = false) String search,
            @Parameter(hidden = true) Pageable pageable) {
        return cardService.listMyCards(status, search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить мою карту", description = "Возвращает карту текущего пользователя по id.")
    public CardResponse getMyCard(@PathVariable Long id) {
        return cardService.getMyCard(id);
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Получить баланс карты", description = "Возвращает текущий баланс выбранной карты пользователя.")
    public BalanceResponse getBalance(@PathVariable Long id) {
        return cardService.getBalance(id);
    }

    @PostMapping("/{id}/block-request")
    @Operation(summary = "Запросить блокировку карты", description = "Создает заявку на блокировку выбранной карты текущего пользователя.")
    public BlockRequestResponse requestBlock(@PathVariable Long id) {
        return blockRequestService.requestBlock(id);
    }
}
