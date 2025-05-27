package me.modkzl.validation.payment;

import jakarta.validation.constraints.NotNull;
import me.modkzl.dto.PaymentDTO;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.exception.EValidationException;
import me.modkzl.validation.IValidatorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentValidator implements IValidatorHandler<PaymentDTO> {

    @Override
    public boolean isApplicable(@NotNull Object object) {
        return object instanceof PaymentDTO;
    }

    @Override
    public void validate(@NotNull PaymentDTO objectToValidate) {
        if (objectToValidate.getPaymentType() == null) {
            throw new IllegalArgumentException("Payment type must not be null");
        }

        if (objectToValidate.getAmount() == null || objectToValidate.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be a positive decimal");
        }

        switch (objectToValidate.getPaymentType()) {
            case TYPE1 -> validateTypeOnePayment(objectToValidate);
            case TYPE2 -> validateTypeTwoPayment(objectToValidate);
            case TYPE3 -> validateTypeThreePayment(objectToValidate);
            default -> EValidationException.PAYMENT_TYPE_NOT_SUPPORTED.throwException(objectToValidate.getPaymentType());
        }
    }

    private void validateTypeOnePayment(@NotNull PaymentDTO objectToValidate) {
        if (!ECurrency.EUR.equals(objectToValidate.getCurrency())) {
            EValidationException.INVALID_PAYMENT_TYPE_CURRENCY.throwException(EPaymentType.TYPE1, objectToValidate.getCurrency());
        }
        if (StringUtils.isBlank(objectToValidate.getDetails())) {
            throw new IllegalArgumentException("Details must not be empty for TYPE1 payments");
        }
    }

    private void validateTypeTwoPayment(@NotNull PaymentDTO objectToValidate) {
        if (!ECurrency.USD.equals(objectToValidate.getCurrency())) {
            EValidationException.INVALID_PAYMENT_TYPE_CURRENCY.throwException(EPaymentType.TYPE2, objectToValidate.getCurrency());
        }
    }

    private void validateTypeThreePayment(PaymentDTO objectToValidate) {
        if (!(ECurrency.EUR.equals(objectToValidate.getCurrency()) || ECurrency.USD.equals(objectToValidate.getCurrency()))) {
            EValidationException.INVALID_PAYMENT_TYPE_CURRENCY.throwException(EPaymentType.TYPE3);
        }
        if (StringUtils.isBlank(objectToValidate.getDetails())) {
            EValidationException.BIC_MUST_NOT_BE_EMPTY_FOR_PAYMENT_TYPE3.throwException(EPaymentType.TYPE3);
        }
    }
}
