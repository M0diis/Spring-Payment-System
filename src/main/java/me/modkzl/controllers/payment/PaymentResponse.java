package me.modkzl.controllers.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.utils.SwaggerConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class PaymentResponse {
    private final Long id;
    private final EPaymentType paymentType;
    private final ECurrency currency;
    private final BigDecimal amount;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    private final String debtorIban;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    private final String creditorIban;
    private final String details;
    @Schema(example = SwaggerConstants.BIC_EXAMPLE)
    private final String creditorBankBic;
    private final boolean canceled;
    private final BigDecimal cancelationFee;
    private final LocalDateTime createdAt;
    private final LocalDateTime canceledAt;
}
