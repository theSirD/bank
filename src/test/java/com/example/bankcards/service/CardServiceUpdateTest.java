package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceUpdateTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private com.example.bankcards.util.CardEncryptionService cardEncryptionService;

    @InjectMocks
    private CardService cardService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void updateCard_updatesExpiryFields() {
        Card card = buildCard(1L, YearMonth.now().plusMonths(6), CardStatus.ACTIVE);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        cardService.updateCard(1L, new CardUpdateRequest(12, 2030));

        assertEquals(12, card.getExpiryMonth());
        assertEquals(2030, card.getExpiryYear());
    }

    @Test
    void updateCard_setsExpiredForPastExpiry() {
        Card card = buildCard(2L, YearMonth.now().plusMonths(1), CardStatus.ACTIVE);
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        cardService.updateCard(2L, new CardUpdateRequest(lastMonth.getMonthValue(), lastMonth.getYear()));

        assertEquals(CardStatus.EXPIRED, card.getStatus());
    }

    @Test
    void updateCard_keepsBlockedStatus() {
        Card card = buildCard(3L, YearMonth.now().plusMonths(2), CardStatus.BLOCKED);
        when(cardRepository.findById(3L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        cardService.updateCard(3L, new CardUpdateRequest(12, 2030));

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    void updateCard_keepsBlockRequestedStatus() {
        Card card = buildCard(4L, YearMonth.now().plusMonths(2), CardStatus.BLOCK_REQUESTED);
        when(cardRepository.findById(4L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        cardService.updateCard(4L, new CardUpdateRequest(12, 2030));

        assertEquals(CardStatus.BLOCK_REQUESTED, card.getStatus());
    }

    @Test
    void cardUpdateRequest_emptyPayloadFailsValidation() {
        CardUpdateRequest request = new CardUpdateRequest(null, null);
        var violations = validator.validate(request);
        assertTrue(violations.stream()
                .anyMatch(v -> "At least one field must be provided for update".equals(v.getMessage())));
    }

    private Card buildCard(Long id, YearMonth expiry, CardStatus status) {
        Card card = new Card();
        card.setId(id);
        card.setExpiryMonth(expiry.getMonthValue());
        card.setExpiryYear(expiry.getYear());
        card.setStatus(status);
        card.setBalance(BigDecimal.ZERO);
        card.setPanLast4("1111");
        User owner = new User();
        owner.setId(10L);
        owner.setUsername("user");
        card.setOwner(owner);
        return card;
    }
}
