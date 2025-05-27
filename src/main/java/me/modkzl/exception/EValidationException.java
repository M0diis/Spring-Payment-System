package me.modkzl.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EValidationException {
    INVALID_PAYMENT_TYPE_CURRENCY("100", "PAYMENTS_1"),
    INVALID_PAYMENT_TYPE_CURRENCY_USD_EUR("101", "PAYMENTS_2"),
    BIC_MUST_NOT_BE_EMPTY_FOR_PAYMENT_TYPE3("102", "PAYMENTS_3"),
    PAYMENT_DOES_NOT_EXIST("103", "PAYMENTS_4"),
    PAYMENT_ALREADY_CANCELLED("104", "PAYMENTS_5"),
    PAYMENT_TYPE_NOT_SUPPORTED("105", "PAYMENTS_6"),
    PAYMENT_CANCELLATION_NOT_ALLOWED("105", "PAYMENTS_7");

    private final String errorCode;
    private final String messageCode;
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private Object[] arguments;

    public void throwException(Object... arguments) {
        this.arguments = arguments;
        throw new ValidationException(this);
    }

    public ValidationException newException(Object... arguments) {
        this.arguments = arguments;
        return new ValidationException(this);
    }
}
