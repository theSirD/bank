package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "admin-card-controller", description = "Административное управление банковскими картами")
public class AdminCardController {

    private final CardService cardService;

    @PostMapping
    @Operation(summary = "Создать карту", description = "Создает карту для выбранного владельца с исходным балансом и сроком действия.")
    public CardResponse createCard(@Valid @RequestBody CardCreateRequest request) {
        return cardService.createCard(request);
    }

    @GetMapping
    @PageableAsQueryParam
    @Operation(
            summary = "Список карт",
            description = "Возвращает постраничный список карт с фильтрами. "
                    + "search работает по последним 4 цифрам (panLast4), поддерживает частичное совпадение.")
    public Page<CardResponse> listCards(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) CardStatus status,
            @Parameter(description = "Поиск по последним 4 цифрам карты (panLast4), например 4444 или 44")
            @RequestParam(required = false) String search,
            @Parameter(hidden = true) Pageable pageable) {
        return cardService.listAllCards(ownerId, status, search, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по id", description = "Возвращает детальную информацию по карте для администратора.")
    public CardResponse getCard(@PathVariable Long id) {
        return cardService.getCardForAdmin(id);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Переводит карту в статус ACTIVE, если это допустимо бизнес-правилами.")
    public CardResponse activate(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "Переводит карту в статус BLOCKED.")
    public CardResponse block(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить параметры карты", description = "Обновляет редактируемые поля карты (например срок действия).")
    public CardResponse update(@PathVariable Long id, @Valid @RequestBody CardUpdateRequest request) {
        return cardService.updateCard(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить карту", description = "Удаляет карту по id. При связанных данных может вернуть конфликт целостности.")
    public void delete(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}
