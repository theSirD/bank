package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardCreateRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.CardUpdateRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.AccessDeniedBusinessException;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.CardSpecification;
import com.example.bankcards.util.CardEncryptionService;
import com.example.bankcards.util.PanValidator;
import com.example.bankcards.util.SecurityUtils;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardEncryptionService cardEncryptionService;

    @Transactional
    public CardResponse createCard(CardCreateRequest request) {
        PanValidator.validate(request.pan());
        User owner = userService.findUser(request.ownerId());
        Card card = new Card();
        card.setOwner(owner);
        card.setEncryptedPan(cardEncryptionService.encrypt(request.pan()));
        card.setPanLast4(PanValidator.extractLast4(request.pan()));
        card.setExpiryMonth(request.expiryMonth());
        card.setExpiryYear(request.expiryYear());
        card.setBalance(request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO);
        card.setStatus(resolveInitialStatus(card));
        return CardMapper.toResponse(cardRepository.save(card));
    }

    @Transactional(readOnly = true)
    public CardResponse getCardForAdmin(Long id) {
        Card card = findCard(id);
        syncExpiredStatus(card);
        return CardMapper.toResponse(card);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> listAllCards(Long ownerId, CardStatus status, String search, Pageable pageable) {
        return cardRepository.findAll(CardSpecification.withFilters(ownerId, status, search), pageable)
                .map(card -> {
                    syncExpiredStatus(card);
                    return CardMapper.toResponse(card);
                });
    }

    @Transactional
    public CardResponse activateCard(Long id) {
        Card card = findCard(id);
        syncExpiredStatus(card);
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new BusinessException("Cannot activate expired card");
        }
        card.setStatus(CardStatus.ACTIVE);
        return CardMapper.toResponse(cardRepository.save(card));
    }

    @Transactional
    public CardResponse blockCard(Long id) {
        Card card = findCard(id);
        card.setStatus(CardStatus.BLOCKED);
        return CardMapper.toResponse(cardRepository.save(card));
    }

    @Transactional
    public CardResponse updateCard(Long id, CardUpdateRequest request) {
        Card card = findCard(id);
        if (request.expiryMonth() != null) {
            card.setExpiryMonth(request.expiryMonth());
        }
        if (request.expiryYear() != null) {
            card.setExpiryYear(request.expiryYear());
        }
        card.setStatus(resolveInitialStatus(card));
        return CardMapper.toResponse(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found");
        }
        cardRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CardResponse> listMyCards(CardStatus status, String search, Pageable pageable) {
        Long userId = SecurityUtils.currentUserId();
        Page<Card> page;
        if (search != null && !search.isBlank()) {
            page = cardRepository.findByOwnerIdAndPanLast4Containing(userId, search.trim(), pageable);
        } else if (status != null) {
            page = cardRepository.findByOwnerIdAndStatus(userId, status, pageable);
        } else {
            page = cardRepository.findByOwnerId(userId, pageable);
        }
        return page.map(card -> {
            syncExpiredStatus(card);
            return CardMapper.toResponse(card);
        });
    }

    @Transactional(readOnly = true)
    public CardResponse getMyCard(Long id) {
        Card card = cardRepository.findByIdAndOwnerId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        syncExpiredStatus(card);
        return CardMapper.toResponse(card);
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(Long id) {
        Card card = cardRepository.findByIdAndOwnerId(id, SecurityUtils.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        syncExpiredStatus(card);
        return new BalanceResponse(card.getBalance());
    }

    public Card findCard(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    public Card findCardForUser(Long cardId, Long userId) {
        return cardRepository.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    public void syncExpiredStatus(Card card) {
        if (card.isExpiredByDate() && card.getStatus() != CardStatus.BLOCKED) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        }
    }

    private CardStatus resolveInitialStatus(Card card) {
        return card.isExpiredByDate() ? CardStatus.EXPIRED : CardStatus.ACTIVE;
    }

    public void ensureOwnerOrAdmin(Card card) {
        if (SecurityUtils.hasRole("ROLE_ADMIN")) {
            return;
        }
        if (!card.getOwner().getId().equals(SecurityUtils.currentUserId())) {
            throw new AccessDeniedBusinessException("Card does not belong to current user");
        }
    }
}
