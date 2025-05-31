package me.modkzl.enums;

import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Optional;

public enum EPaymentType {
    TYPE1,
    TYPE2,
    TYPE3,
    TYPE4 // Unused
    ;

    public static Optional<EPaymentType> fromString(@Nullable String type) {
        return Arrays.stream(values())
                .filter(paymentType -> paymentType.name().equalsIgnoreCase(type))
                .findFirst();
    }
}
