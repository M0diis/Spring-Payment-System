package me.modkzl.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;

import java.math.BigDecimal;

@Builder
@Getter
@ToString
public class PaymentDTO {
    private final EPaymentType paymentType;
    private final ECurrency currency;
    private final BigDecimal amount;
    private final String debtorIban;
    private final String creditorIban;
    private final String details;
    private final String creditorBankBic;
}
