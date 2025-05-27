package me.modkzl.validation.payment;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IBANValidator implements ConstraintValidator<ValidIBAN, String> {
    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow null or empty values, use @NotNull for mandatory fields
        }
        return IBAN_PATTERN.matcher(value).matches();
    }
}