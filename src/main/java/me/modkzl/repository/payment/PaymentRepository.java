package me.modkzl.repository.payment;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.controllers.payment.PaymentFilterCriteria;
import me.modkzl.enums.ECurrency;
import me.modkzl.enums.EPaymentType;
import me.modkzl.jooq.public_.tables.records.PaymentRecord;
import me.modkzl.utils.ConditionBuilder;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

import static me.modkzl.jooq.public_.tables.Payment.PAYMENT;

@Repository
@RequiredArgsConstructor
public class PaymentRepository {
    private final DSLContext dslContext;

    public Long save(@NotNull PaymentRecord payment) {
        return dslContext.insertInto(PAYMENT)
                .set(payment)
                .returning(PAYMENT.ID)
                .fetchOne()
                .getValue(PAYMENT.ID);
    }

    public Collection<PaymentRecord> findAll(@NotNull PaymentFilterCriteria criteria) {
        Condition condition = new ConditionBuilder()
                .andIn(PAYMENT.ID, criteria.getIds())
                .andIn(PAYMENT.TYPE, criteria.getPaymentTypes() != null ?
                        criteria.getPaymentTypes().stream()
                                .map(EPaymentType::name)
                                .toList() : null)
                .andIn(PAYMENT.CURRENCY, criteria.getCurrencies() != null ?
                        criteria.getCurrencies().stream()
                                .map(ECurrency::name)
                                .toList() : null)
                .andGreaterOrEqual(PAYMENT.AMOUNT, criteria.getAmountFrom())
                .andLessOrEqual(PAYMENT.AMOUNT, criteria.getAmountTo())
                .andEquals(PAYMENT.DEBTOR_IBAN, criteria.getDebtorIban())
                .andEquals(PAYMENT.CREDITOR_IBAN, criteria.getCreditorIban())
                .andEquals(PAYMENT.CREDITOR_BIC, criteria.getCreditorBic())
                .andEquals(PAYMENT.DETAILS, criteria.getDetails())
                .andEquals(PAYMENT.CANCELLED, criteria.getCancelled())
                .andGreaterOrEqual(PAYMENT.CANCELLATION_FEE, criteria.getCancellationFeeFrom())
                .andLessOrEqual(PAYMENT.CANCELLATION_FEE, criteria.getCancellationFeeTo())
                .andGreaterOrEqual(PAYMENT.CREATED_AT, criteria.getCreatedAtFrom())
                .andLessOrEqual(PAYMENT.CREATED_AT, criteria.getCreatedAtTo())
                .andGreaterOrEqual(PAYMENT.CANCELLED_AT, criteria.getCanceledAtFrom())
                .andLessOrEqual(PAYMENT.CANCELLED_AT, criteria.getCanceledAtTo())
                .build();

        return dslContext.selectFrom(PAYMENT)
                .where(condition)
                .fetchInto(PaymentRecord.class);
    }

    public Optional<PaymentRecord> findById(@NotNull Long id) {
        return dslContext.selectFrom(PAYMENT)
                .where(PAYMENT.ID.eq(id))
                .fetchOptionalInto(PaymentRecord.class);
    }
}