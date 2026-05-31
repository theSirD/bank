package com.example.bankcards.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CardMaskUtilTest {

    @Test
    void mask_formatsLastFourDigits() {
        assertEquals("**** **** **** 1234", CardMaskUtil.mask("4111111111111234"));
    }

    @Test
    void maskFromLast4() {
        assertEquals("**** **** **** 5678", CardMaskUtil.maskFromLast4("5678"));
    }
}
