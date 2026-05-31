package com.example.bankcards.util;

import com.example.bankcards.exception.BusinessException;

public final class PanValidator {

    private PanValidator() {
    }

    public static void validate(String pan) {
        if (pan == null || !pan.matches("\\d{16}")) {
            throw new BusinessException("Card number must be 16 digits");
        }
        if (!luhnCheck(pan)) {
            throw new BusinessException("Invalid card number (Luhn check failed)");
        }
    }

    public static String extractLast4(String pan) {
        return pan.substring(pan.length() - 4);
    }

    private static boolean luhnCheck(String pan) {
        int sum = 0;
        boolean alternate = false;
        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(pan.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
