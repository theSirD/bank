package com.example.bankcards.util;

public final class CardMaskUtil {

    private CardMaskUtil() {
    }

    public static String mask(String pan) {
        if (pan == null || pan.length() < 4) {
            return "**** **** **** ****";
        }
        String last4 = pan.substring(pan.length() - 4);
        return "**** **** **** " + last4;
    }

    public static String maskFromLast4(String panLast4) {
        return "**** **** **** " + panLast4;
    }
}
