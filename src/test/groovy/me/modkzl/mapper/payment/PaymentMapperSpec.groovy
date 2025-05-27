package me.modkzl.mapper.payment

import me.modkzl.controllers.payment.PaymentRequest
import me.modkzl.domain.payment.PaymentDomain
import me.modkzl.dto.PaymentDTO
import me.modkzl.enums.ECurrency
import me.modkzl.enums.EPaymentType
import me.modkzl.jooq.public_.tables.records.PaymentRecord
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class PaymentMapperSpec extends Specification {

    @Subject
    def paymentMapper = new PaymentMapper()

    def "should map PaymentEntity to PaymentDomain"() {
        given:
        def paymentEntity = new PaymentRecord(
                id: 1L,
                type: EPaymentType.TYPE1,
                amount: BigDecimal.TEN,
                currency: ECurrency.EUR,
                debtorIban: "DE1234567890",
                creditorIban: "DE0987654321",
                details: "Payment details",
                creditorBic: "BIC12345",
                cancelled: false,
                cancellationFee: BigDecimal.ZERO,
                createdAt: LocalDateTime.now(),
                cancelledAt: null
        )

        when:
        def result = paymentMapper.mapRecordToDomain(paymentEntity)

        then:
        with(result) {
            id == paymentEntity.id
            paymentType.name() == paymentEntity.type
            amount == paymentEntity.amount
            currency.name() == paymentEntity.currency
            debtorIban == paymentEntity.debtorIban
            creditorIban == paymentEntity.creditorIban
            details == paymentEntity.details
            creditorBankBic == paymentEntity.creditorBic
            cancelled == paymentEntity.cancelled
            cancellationFee == paymentEntity.cancellationFee
            createdAt == paymentEntity.createdAt
            canceledAt == paymentEntity.cancelledAt
        }
    }

    def "should map PaymentDomain to PaymentResponse"() {
        given:
        def paymentDomain = PaymentDomain.builder()
                .id(1L)
                .paymentType(EPaymentType.TYPE1)
                .amount(BigDecimal.TEN)
                .currency(ECurrency.EUR)
                .debtorIban("DE1234567890")
                .creditorIban("DE0987654321")
                .details("Payment details")
                .creditorBankBic("BIC12345")
                .cancelled(false)
                .cancellationFee(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .canceledAt(null)
                .build()

        when:
        def result = paymentMapper.mapDomainToResponse(paymentDomain)

        then:
        with(result) {
            id == paymentDomain.id
            paymentType == paymentDomain.paymentType
            amount == paymentDomain.amount
            currency == paymentDomain.currency
            debtorIban == paymentDomain.debtorIban
            creditorIban == paymentDomain.creditorIban
            details == paymentDomain.details
            creditorBankBic == paymentDomain.creditorBankBic
            canceled == paymentDomain.cancelled
            cancelationFee == paymentDomain.cancellationFee
            createdAt == paymentDomain.createdAt
            canceledAt == paymentDomain.canceledAt
        }
    }

    def "should map PaymentDTO to PaymentEntity"() {
        given:
        def paymentDTO = PaymentDTO.builder()
                .paymentType(EPaymentType.TYPE1)
                .amount(BigDecimal.TEN)
                .currency(ECurrency.EUR)
                .debtorIban("DE1234567890")
                .creditorIban("DE0987654321")
                .details("Payment details")
                .build()

        when:
        def result = paymentMapper.mapDtoToRecord(paymentDTO)

        then:
        with(result) {
            type == paymentDTO.paymentType.name()
            amount == paymentDTO.amount
            currency == paymentDTO.currency.name()
            debtorIban == paymentDTO.debtorIban
            creditorIban == paymentDTO.creditorIban
            details == paymentDTO.details
        }
    }

    def "should map PaymentRequest to PaymentDTO"() {
        given:
        def paymentRequest = PaymentRequest.builder()
                .paymentType(EPaymentType.TYPE1)
                .amount(BigDecimal.TEN)
                .currency(ECurrency.EUR)
                .debtorIban("DE1234567890")
                .creditorIban("DE0987654321")
                .details("Payment details")
                .creditorBankBic("BIC12345")
                .build()

        when:
        def result = paymentMapper.mapRequestToDto(paymentRequest)

        then:
        with(result) {
            paymentType == paymentRequest.paymentType
            amount == paymentRequest.amount
            currency == paymentRequest.currency
            debtorIban == paymentRequest.debtorIban
            creditorIban == paymentRequest.creditorIban
            details == paymentRequest.details
            creditorBankBic == paymentRequest.creditorBankBic
        }
    }
}