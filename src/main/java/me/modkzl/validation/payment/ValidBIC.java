package me.modkzl.validation.payment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import me.modkzl.utils.SwaggerConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BICValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBIC {
    String message() default "Invalid BIC code. Format example: " + SwaggerConstants.BIC_EXAMPLE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}