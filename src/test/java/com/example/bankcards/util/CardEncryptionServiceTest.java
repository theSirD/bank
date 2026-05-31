package com.example.bankcards.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardEncryptionServiceTest {

    private CardEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new CardEncryptionService("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
    }

    @Test
    void encryptDecrypt_roundTrip() {
        String pan = "4111111111111111";
        String encrypted = encryptionService.encrypt(pan);
        assertNotEquals(pan, encrypted);
        assertEquals(pan, encryptionService.decrypt(encrypted));
    }
}
