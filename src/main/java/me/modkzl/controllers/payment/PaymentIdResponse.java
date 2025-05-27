package me.modkzl.controllers.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentIdResponse(@NotNull Long paymentId) {
}
