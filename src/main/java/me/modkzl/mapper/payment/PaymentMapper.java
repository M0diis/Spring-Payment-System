package me.modkzl.mapper.payment;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.modkzl.controllers.payment.PaymentRequest;
import me.modkzl.controllers.payment.PaymentResponse;
import me.modkzl.domain.payment.PaymentDomain;
import me.modkzl.dto.PaymentDTO;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.jooq.public_.tables.records.PaymentRecord;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public @Nullable PaymentDomain mapRecordToDomain(@Nullable PaymentRecord paymentRecord) {
        if (paymentRecord == null) {
            return null;
        }

        return PaymentDomain.builder()
                .id(paymentRecord.getId())
                .amount(paymentRecord.getAmount())
                .currency(ECurrency.valueOf(paymentRecord.getCurrency()))
                .paymentType(EPaymentType.valueOf(paymentRecord.getType()))
                .debtorIban(paymentRecord.getDebtorIban())
                .creditorIban(paymentRecord.getCreditorIban())
                .details(paymentRecord.getDetails())
                .creditorBankBic(paymentRecord.getCreditorBic())
                .cancelled(paymentRecord.getCancelled())
                .cancellationFee(paymentRecord.getCancellationFee())
                .createdAt(paymentRecord.getCreatedAt())
                .canceledAt(paymentRecord.getCancelledAt())
                .build();
    }

    public @Nullable PaymentResponse mapDomainToResponse(@Nullable PaymentDomain paymentEntity) {
        if (paymentEntity == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(paymentEntity.getId())
                .amount(paymentEntity.getAmount())
                .currency(paymentEntity.getCurrency())
                .paymentType(paymentEntity.getPaymentType())
                .debtorIban(paymentEntity.getDebtorIban())
                .creditorIban(paymentEntity.getCreditorIban())
                .details(paymentEntity.getDetails())
                .creditorBankBic(paymentEntity.getCreditorBankBic())
                .canceled(paymentEntity.isCancelled())
                .cancelationFee(paymentEntity.getCancellationFee())
                .createdAt(paymentEntity.getCreatedAt())
                .canceledAt(paymentEntity.getCanceledAt())
                .build();
    }

    public @NotNull PaymentRecord mapDtoToRecord(@Nullable PaymentDTO paymentDto) {
        if (paymentDto == null) {
            throw new IllegalArgumentException("PaymentDTO cannot be null");
        }

        return new PaymentRecord()
                .setType(paymentDto.getPaymentType().name())
                .setAmount(paymentDto.getAmount())
                .setCurrency(paymentDto.getCurrency().name())
                .setDebtorIban(paymentDto.getDebtorIban())
                .setCreditorIban(paymentDto.getCreditorIban())
                .setDetails(paymentDto.getDetails());
    }

    public @NotNull PaymentDTO mapRequestToDto(@NotNull PaymentRequest request) {
        return PaymentDTO.builder()
                .paymentType(request.getPaymentType())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .debtorIban(request.getDebtorIban())
                .creditorIban(request.getCreditorIban())
                .details(request.getDetails())
                .creditorBankBic(request.getCreditorBankBic())
                .build();
    }
}
