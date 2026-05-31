package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardServiceStatusTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private com.example.bankcards.util.CardEncryptionService cardEncryptionService;

    @InjectMocks
    private CardService cardService;

    @Test
    void syncExpiredStatus_setsExpiredWhenPastExpiry() {
        Card card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        card.setExpiryMonth(lastMonth.getMonthValue());
        card.setExpiryYear(lastMonth.getYear());
        card.setOwner(new User());

        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        cardService.syncExpiredStatus(card);

        assertEquals(CardStatus.EXPIRED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void isExpiredByDate_onEntity() {
        Card card = new Card();
        YearMonth future = YearMonth.now().plusYears(2);
        card.setExpiryMonth(future.getMonthValue());
        card.setExpiryYear(future.getYear());
        assertTrue(!card.isExpiredByDate());
    }
}
