package me.modkzl.service;

import jakarta.validation.constraints.NotNull;
import me.modkzl.enums.EPaymentType;
import me.modkzl.exception.EValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CancellationFeeCalculator {
    public @NotNull BigDecimal calculate(@NotNull EPaymentType paymentType, @NotNull BigDecimal factor) {
        BigDecimal cancellationFee = switch (paymentType) {
            case TYPE1 -> factor.multiply(BigDecimal.valueOf(0.05));
            case TYPE2 -> factor.multiply(BigDecimal.valueOf(0.1));
            case TYPE3 -> factor.multiply(BigDecimal.valueOf(0.15));
            default -> throw EValidationException.PAYMENT_TYPE_NOT_SUPPORTED.newException(paymentType.name());
        };

        return cancellationFee.max(BigDecimal.ZERO);
    }
}
