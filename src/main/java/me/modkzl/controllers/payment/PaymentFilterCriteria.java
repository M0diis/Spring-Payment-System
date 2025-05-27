package me.modkzl.controllers.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.utils.SwaggerConstants;
import me.modkzl.validation.payment.ValidBIC;
import me.modkzl.validation.payment.ValidIBAN;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@ToString
public final class PaymentFilterCriteria {
    @JsonProperty("ids")
    private List<Long> ids;
    @JsonProperty("payment_types")
    private List<EPaymentType> paymentTypes;
    @JsonProperty("currencies")
    private List<ECurrency> currencies;
    @JsonProperty("amount_from")
    private BigDecimal amountFrom;
    @JsonProperty("amount_to")
    private BigDecimal amountTo;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    @ValidIBAN
    @JsonProperty("debtor_iban")
    private String debtorIban;
    @Schema(example = SwaggerConstants.IBAN_EXAMPLE)
    @ValidIBAN
    @JsonProperty("creditor_iban")
    private String creditorIban;
    @Schema(example = SwaggerConstants.BIC_EXAMPLE)
    @ValidBIC
    @JsonProperty("creditor_bic")
    private String creditorBic;
    @JsonProperty("details")
    private String details;
    @JsonProperty("cancelled")
    private Boolean cancelled;
    @JsonProperty("cancellation_fee_from")
    private BigDecimal cancellationFeeFrom;
    @JsonProperty("cancellation_fee_to")
    private BigDecimal cancellationFeeTo;
    @JsonProperty("created_at_from")
    private LocalDateTime createdAtFrom;
    @JsonProperty("created_at_to")
    private LocalDateTime createdAtTo;
    @JsonProperty("canceled_at_from")
    private LocalDateTime canceledAtFrom;
    @JsonProperty("canceled_at_to")
    private LocalDateTime canceledAtTo;
}
