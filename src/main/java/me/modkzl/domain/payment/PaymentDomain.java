package me.modkzl.domain.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class PaymentDomain {
    private final Long id;
    private final EPaymentType paymentType;
    private final ECurrency currency;
    private final BigDecimal amount;
    private final String debtorIban;
    private final String creditorIban;
    private final String details;
    private final String creditorBankBic;
    private final boolean cancelled;
    private final BigDecimal cancellationFee;
    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;
}