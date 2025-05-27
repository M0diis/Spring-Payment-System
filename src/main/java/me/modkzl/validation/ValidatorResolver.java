package me.modkzl.validation;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.validation.ValidatorHandler;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Resolves {@link ValidatorHandler}
 */
@Component
@RequiredArgsConstructor
public class ValidatorResolver {
    private final ApplicationContext context;
    private Set<IValidatorHandler<?>> validatorHandlers;

    @SuppressWarnings("unchecked")
    public <T> void validate(@Nullable T object) {
        if (this.validatorHandlers == null) {
            initializeValidators();
        }

        List<IValidatorHandler<?>> applicableValidators = validatorHandlers.stream()
                .filter(handler -> handler.isApplicable(object))
                .toList();

        if (CollectionUtils.isEmpty(applicableValidators)) {
            throw new IllegalStateException("Validator handler could not be found for object: " + object);
        }

        applicableValidators.stream()
                .map(handler -> (IValidatorHandler<T>) handler)
                .forEach(handler -> handler.validate(object));
    }

    private void initializeValidators() {
        this.validatorHandlers = Stream.of(context.getBeanNamesForType(IValidatorHandler.class))
                .map(beanName -> (IValidatorHandler<?>) context.getBean(beanName, IValidatorHandler.class))
                .collect(Collectors.toSet());
    }
}
