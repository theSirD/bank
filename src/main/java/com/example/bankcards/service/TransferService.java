package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.dto.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.AccessDeniedBusinessException;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.SecurityUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardRepository cardRepository;
    private final CardService cardService;

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        Long userId = SecurityUtils.currentUserId();
        if (request.fromCardId().equals(request.toCardId())) {
            throw new BusinessException("Cannot transfer to the same card", HttpStatus.CONFLICT);
        }
        Card from = cardRepository.findById(request.fromCardId())
                .orElseThrow(() -> new BusinessException("Source card not found"));
        Card to = cardRepository.findById(request.toCardId())
                .orElseThrow(() -> new BusinessException("Destination card not found"));
        if (!from.getOwner().getId().equals(userId) || !to.getOwner().getId().equals(userId)) {
            throw new AccessDeniedBusinessException("Transfers allowed only between your own cards");
        }
        cardService.syncExpiredStatus(from);
        cardService.syncExpiredStatus(to);
        validateForTransfer(from);
        validateForTransfer(to);
        if (from.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException();
        }
        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));
        cardRepository.save(from);
        cardRepository.save(to);
        return new TransferResponse(from.getId(), to.getId(), request.amount(), Instant.now());
    }

    private void validateForTransfer(Card card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Card must be ACTIVE for transfers: " + card.getStatus(), HttpStatus.CONFLICT);
        }
        if (card.isExpiredByDate()) {
            throw new BusinessException("Card is expired");
        }
    }
}
