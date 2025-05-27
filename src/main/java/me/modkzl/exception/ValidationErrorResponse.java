package me.modkzl.exception;

import jakarta.validation.constraints.NotNull;

public record ValidationErrorResponse(@NotNull String errorCode, @NotNull String errorMessage) {
}
