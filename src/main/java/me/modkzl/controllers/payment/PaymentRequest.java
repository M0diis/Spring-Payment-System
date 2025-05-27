package me.modkzl.controllers.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.utils.SwaggerConstants;
import me.modkzl.validation.payment.ValidBIC;
import me.modkzl.validation.payment.ValidIBAN;

import java.math.BigDecimal;

@Builder
@Getter
@ToString
public final class PaymentRequest {
    @JsonProperty("payment_type")
    private final EPaymentType paymentType;
    @JsonProperty("currency")
    private final ECurrency currency;
    @JsonProperty("amount")
    private final BigDecimal amount;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    @JsonProperty("debtor_iban")
    @ValidIBAN
    private final String debtorIban;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    @JsonProperty("creditor_iban")
    @ValidIBAN
    private final String creditorIban;
    @JsonProperty("details")
    private final String details;
    @Schema(example = SwaggerConstants.BIC_EXAMPLE)
    @JsonProperty("creditor_bank_bic")
    @ValidBIC
    private final String creditorBankBic;
}
