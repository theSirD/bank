package com.example.bankcards.service;

import com.example.bankcards.dto.BlockRequestResponse;
import com.example.bankcards.entity.BlockRequest;
import com.example.bankcards.entity.BlockRequestStatus;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.repository.BlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardMaskUtil;
import com.example.bankcards.util.SecurityUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockRequestService {

    private final BlockRequestRepository blockRequestRepository;
    private final CardRepository cardRepository;
    private final CardService cardService;
    private final UserService userService;

    @Transactional
    public BlockRequestResponse requestBlock(Long cardId) {
        Long userId = SecurityUtils.currentUserId();
        Card card = cardService.findCardForUser(cardId, userId);
        cardService.syncExpiredStatus(card);
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BusinessException("Only active cards can be submitted for block request", HttpStatus.CONFLICT);
        }
        blockRequestRepository.findByCardIdAndStatus(cardId, BlockRequestStatus.PENDING)
                .ifPresent(br -> {
                    throw new BusinessException("Pending block request already exists for this card", HttpStatus.CONFLICT);
                });
        BlockRequest blockRequest = new BlockRequest();
        blockRequest.setCard(card);
        blockRequest.setUser(card.getOwner());
        blockRequest.setStatus(BlockRequestStatus.PENDING);
        card.setStatus(CardStatus.BLOCK_REQUESTED);
        cardRepository.save(card);
        BlockRequest saved = blockRequestRepository.save(blockRequest);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BlockRequestResponse> listPending(Pageable pageable) {
        return blockRequestRepository.findByStatus(BlockRequestStatus.PENDING, pageable).map(this::toResponse);
    }

    @Transactional
    public BlockRequestResponse approve(Long requestId) {
        BlockRequest blockRequest = findPendingRequest(requestId);
        blockRequest.setStatus(BlockRequestStatus.APPROVED);
        blockRequest.setResolvedAt(Instant.now());
        blockRequest.setResolvedBy(userService.findUser(SecurityUtils.currentUserId()));
        Card card = blockRequest.getCard();
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
        return toResponse(blockRequestRepository.save(blockRequest));
    }

    @Transactional
    public BlockRequestResponse reject(Long requestId) {
        BlockRequest blockRequest = findPendingRequest(requestId);
        blockRequest.setStatus(BlockRequestStatus.REJECTED);
        blockRequest.setResolvedAt(Instant.now());
        blockRequest.setResolvedBy(userService.findUser(SecurityUtils.currentUserId()));
        Card card = blockRequest.getCard();
        card.setStatus(card.isExpiredByDate() ? CardStatus.EXPIRED : CardStatus.ACTIVE);
        cardRepository.save(card);
        return toResponse(blockRequestRepository.save(blockRequest));
    }

    private BlockRequest findPendingRequest(Long requestId) {
        BlockRequest blockRequest = blockRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Block request not found"));
        if (blockRequest.getStatus() != BlockRequestStatus.PENDING) {
            throw new BusinessException("Block request is not pending");
        }
        return blockRequest;
    }

    private BlockRequestResponse toResponse(BlockRequest blockRequest) {
        Card card = blockRequest.getCard();
        return new BlockRequestResponse(
                blockRequest.getId(),
                card.getId(),
                card.getOwner().getUsername(),
                CardMaskUtil.maskFromLast4(card.getPanLast4()),
                blockRequest.getStatus(),
                blockRequest.getCreatedAt(),
                blockRequest.getResolvedAt());
    }
}
