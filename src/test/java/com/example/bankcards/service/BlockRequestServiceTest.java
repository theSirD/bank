package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtils;
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
class BlockRequestServiceTest {

    @Mock
    private BlockRequestRepository blockRequestRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardService cardService;

    @Mock
    private UserService userService;

    @InjectMocks
    private BlockRequestService blockRequestService;

    private MockedStatic<SecurityUtils> securityUtils;

    @BeforeEach
    void setUp() {
        securityUtils = Mockito.mockStatic(SecurityUtils.class);
        securityUtils.when(SecurityUtils::currentUserId).thenReturn(1L);
    }

    @AfterEach
    void tearDown() {
        securityUtils.close();
    }

    @Test
    void approve_setsCardBlocked() {
        Card card = new Card();
        card.setId(10L);
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        card.setPanLast4("4242");
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("user");
        card.setOwner(owner);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setId(5L);
        blockRequest.setCard(card);
        blockRequest.setUser(owner);
        blockRequest.setStatus(BlockRequestStatus.PENDING);

        when(blockRequestRepository.findById(5L)).thenReturn(Optional.of(blockRequest));
        when(userService.findUser(1L)).thenReturn(owner);
        when(blockRequestRepository.save(any(BlockRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        blockRequestService.approve(5L);

        assertEquals(BlockRequestStatus.APPROVED, blockRequest.getStatus());
        assertEquals(CardStatus.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void reject_setsCardActive() {
        Card card = new Card();
        card.setId(10L);
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        card.setPanLast4("4242");
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("user");
        card.setOwner(owner);

        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setId(6L);
        blockRequest.setCard(card);
        blockRequest.setUser(owner);
        blockRequest.setStatus(BlockRequestStatus.PENDING);

        when(blockRequestRepository.findById(6L)).thenReturn(Optional.of(blockRequest));
        when(userService.findUser(1L)).thenReturn(owner);
        when(blockRequestRepository.save(any(BlockRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        blockRequestService.reject(6L);

        assertEquals(BlockRequestStatus.REJECTED, blockRequest.getStatus());
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
    }
}
