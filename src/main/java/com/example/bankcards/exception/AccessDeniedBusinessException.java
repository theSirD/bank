package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedBusinessException extends BusinessException {

    public AccessDeniedBusinessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
