package me.modkzl.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.controllers.payment.PaymentFilterCriteria;
import me.modkzl.domain.payment.PaymentDomain;
import me.modkzl.dto.PaymentDTO;
import me.modkzl.enums.EPaymentType;
import me.modkzl.exception.EValidationException;
import me.modkzl.jooq.public_.tables.records.PaymentRecord;
import me.modkzl.mapper.payment.PaymentMapper;
import me.modkzl.repository.payment.PaymentRepository;
import me.modkzl.utils.TimeProvider;
import me.modkzl.validation.ValidatorResolver;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ValidatorResolver validatorResolver;
    private final NotificationService notificationService;
    private final TimeProvider timeProvider;

    public @NotNull Long create(@NotNull PaymentDTO payment) {
        validatorResolver.validate(payment);

        PaymentRecord paymentEntity = paymentMapper.mapDtoToRecord(payment);

        Long id = paymentRepository.save(paymentEntity);

        notificationService.notify(payment.getPaymentType(), id);

        return id;
    }

    public @NotNull PaymentDomain get(@NotNull Long id) {
        PaymentRecord paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> EValidationException.PAYMENT_DOES_NOT_EXIST.newException(id));

        return paymentMapper.mapRecordToDomain(paymentEntity);
    }

    public @NotNull List<@NotNull PaymentDomain> get(@NotNull PaymentFilterCriteria criteria) {
        return paymentRepository.findAll(criteria)
                .stream()
                .map(paymentMapper::mapRecordToDomain)
                .toList();
    }

    public void cancel(@NotNull Long id) {
        PaymentRecord paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> EValidationException.PAYMENT_DOES_NOT_EXIST.newException(id));

        if (Boolean.TRUE.equals(paymentEntity.getCancelled())) {
            throw EValidationException.PAYMENT_ALREADY_CANCELLED.newException(id);
        }

        LocalDateTime currentTime = timeProvider.getLocalDateTimeNow();
        LocalDateTime creationTime = paymentEntity.getCreatedAt();
        LocalDateTime endOfCreationDay = creationTime.toLocalDate().plusDays(1).atStartOfDay();

        if (currentTime.isAfter(endOfCreationDay)) {
            throw EValidationException.PAYMENT_CANCELLATION_NOT_ALLOWED.newException(id);
        }

        long hoursInSystem = currentTime.getLong(ChronoField.HOUR_OF_DAY) - creationTime.getLong(ChronoField.HOUR_OF_DAY);

        final BigDecimal hoursInSystemBigDecimal = BigDecimal.valueOf(hoursInSystem);

        BigDecimal cancellationFee = switch (EPaymentType.valueOf(paymentEntity.getType())) {
            case TYPE1 -> hoursInSystemBigDecimal.multiply(BigDecimal.valueOf(0.05));
            case TYPE2 -> hoursInSystemBigDecimal.multiply(BigDecimal.valueOf(0.1));
            case TYPE3 -> hoursInSystemBigDecimal.multiply(BigDecimal.valueOf(0.15));
            default -> throw EValidationException.PAYMENT_TYPE_NOT_SUPPORTED.newException(paymentEntity.getType());
        };

        if (cancellationFee.compareTo(BigDecimal.ZERO) < 0) {
            cancellationFee = BigDecimal.ZERO;
        }

        paymentEntity.setCancelled(true);
        paymentEntity.setCancellationFee(cancellationFee);
    }
}
