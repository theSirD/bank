package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedBusinessException;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtils;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransferService transferService;

    private MockedStatic<SecurityUtils> securityUtils;

    private User owner;
    private Card from;
    private Card to;

    @BeforeEach
    void setUp() {
        securityUtils = Mockito.mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::currentUserId).thenReturn(1L);

        owner = new User();
        owner.setId(1L);

        from = activeCard(1L, new BigDecimal("100.00"));
        to = activeCard(2L, BigDecimal.ZERO);

        lenient().doAnswer(inv -> {
                    Card c = inv.getArgument(0);
                    if (c.isExpiredByDate() && c.getStatus() != CardStatus.BLOCKED) {
                        c.setStatus(CardStatus.EXPIRED);
                    }
                    return null;
                })
                .when(cardService)
                .syncExpiredStatus(any(Card.class));
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }

    @Test
    void transfer_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("25.50"));
        var response = transferService.transfer(request);

        assertEquals(new BigDecimal("74.50"), from.getBalance());
        assertEquals(new BigDecimal("25.50"), to.getBalance());
        assertEquals(1L, response.fromCardId());
        assertEquals(2L, response.toCardId());
    }

    @Test
    void transfer_insufficientFunds() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(
                InsufficientFundsException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("500.00"))));
    }

    @Test
    void transfer_notOwnCard() {
        User other = new User();
        other.setId(99L);
        to.setOwner(other);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(
                AccessDeniedBusinessException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("10.00"))));
    }

    @Test
    void transfer_blockedCard() {
        from.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(
                BusinessException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("10.00"))));
    }

    @Test
    void transfer_sameCard() {
        assertThrows(
                BusinessException.class,
                () -> transferService.transfer(new TransferRequest(1L, 1L, new BigDecimal("10.00"))));
    }

    @Test
    void transfer_blockRequestedStatus() {
        from.setStatus(CardStatus.BLOCK_REQUESTED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(
                BusinessException.class,
                () -> transferService.transfer(new TransferRequest(1L, 2L, new BigDecimal("10.00"))));
    }

    private Card activeCard(Long id, BigDecimal balance) {
        Card card = new Card();
        card.setId(id);
        card.setOwner(owner);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        YearMonth future = YearMonth.now().plusYears(2);
        card.setExpiryMonth(future.getMonthValue());
        card.setExpiryYear(future.getYear());
        card.setPanLast4("1111");
        return card;
    }
}
