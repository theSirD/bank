package com.example.bankcards.service;

import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.CardMaskUtil;

public final class CardMapper {

    private CardMapper() {
    }

    public static CardResponse toResponse(Card card) {
        return new CardResponse(
                card.getId(),
                CardMaskUtil.maskFromLast4(card.getPanLast4()),
                card.getOwner().getUsername(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getStatus(),
                card.getBalance());
    }
}
