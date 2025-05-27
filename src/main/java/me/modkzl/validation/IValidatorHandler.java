package me.modkzl.validation;

import jakarta.validation.constraints.NotNull;

public interface IValidatorHandler<T> {

    /**
     * Checks if current handler can validate provided object.
     *
     * @param object to check for validation
     * @return {@code true} if the input argument can be validated, otherwise {@code false}
     */
    boolean isApplicable(@NotNull Object object);

    /**
     * Validates input argument
     *
     * @param objectToValidate object to be validated
     */
    void validate(@NotNull T objectToValidate);
}
