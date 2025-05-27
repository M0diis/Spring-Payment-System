package me.modkzl.exception;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidationException extends RuntimeException {

    private final EValidationException exception;

    protected ValidationException(@NotNull EValidationException exception) {
        super();
        this.exception = exception;
    }
}
