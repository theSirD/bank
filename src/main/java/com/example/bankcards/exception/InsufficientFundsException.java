package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException() {
        super("Insufficient funds on source card", HttpStatus.CONFLICT);
    }
}
