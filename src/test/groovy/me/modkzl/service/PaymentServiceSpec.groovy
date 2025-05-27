package me.modkzl.service

import me.modkzl.controllers.payment.PaymentFilterCriteria
import me.modkzl.domain.payment.PaymentDomain
import me.modkzl.dto.PaymentDTO
import me.modkzl.enums.EPaymentType
import me.modkzl.exception.EValidationException
import me.modkzl.exception.ValidationException
import me.modkzl.jooq.public_.tables.records.PaymentRecord
import me.modkzl.mapper.payment.PaymentMapper

import me.modkzl.repository.payment.PaymentRepository
import me.modkzl.utils.TimeProvider
import me.modkzl.validation.ValidatorResolver
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class PaymentServiceSpec extends Specification {

    def static final CURRENT_TIME = LocalDateTime.of(2025, 05, 1, 12, 0)

    def paymentRepository = Mock(PaymentRepository)
    def paymentMapper = Mock(PaymentMapper)
    def validatorResolver = Mock(ValidatorResolver)
    def notificationService = Mock(NotificationService)
    def timeProvider = Mock(TimeProvider)

    @Subject
    def paymentService = new PaymentService(paymentRepository,
            paymentMapper,
            validatorResolver,
            notificationService,
            timeProvider)

    def setup() {
        timeProvider.getLocalDateTimeNow() >> CURRENT_TIME
    }

    def "should process payment and return ID"() {
        given:
        def paymentDTO = PaymentDTO.builder()
                .paymentType(EPaymentType.TYPE1)
                .amount(BigDecimal.TEN)
                .build()
        def paymentRecord = new PaymentRecord(id: 1L)

        when:
        def result = paymentService.create(paymentDTO)

        then:
        1 * validatorResolver.validate(paymentDTO)
        1 * paymentMapper.mapDtoToRecord(paymentDTO) >> paymentRecord
        1 * paymentRepository.save(paymentRecord) >> 1L
        result == 1L
    }

    def "should get payment by ID"() {
        given:
        def paymentEntity = new PaymentRecord(id: 1L)
        def paymentDomain = PaymentDomain.builder()
                .id(1L)
                .build()

        when:
        def result = paymentService.get(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.of(paymentEntity)
        1 * paymentMapper.mapRecordToDomain(paymentEntity) >> paymentDomain
        result == paymentDomain
    }

    def "should throw exception when payment not found by ID"() {
        when:
        paymentService.get(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.empty()
        def ex = thrown(ValidationException)
        ex.exception == EValidationException.PAYMENT_DOES_NOT_EXIST
    }

    def "should get all payments based on criteria"() {
        given:
        def criteria = PaymentFilterCriteria.builder()
                .ids([1L])
                .build()
        def paymentEntity = new PaymentRecord(id: 1L)
        def paymentDomain = PaymentDomain.builder()
                .id(1L)
                .build()

        when:
        def result = paymentService.get(criteria)

        then:
        1 * paymentRepository.findAll(_ as PaymentFilterCriteria) >> [paymentEntity]
        1 * paymentMapper.mapRecordToDomain(paymentEntity) >> paymentDomain
        result == [paymentDomain]
    }

    def "should cancel payment and calculate cancellation fee with type #paymentType and hours in system #hoursInSystem"() {
        given:
        def paymentEntity = new PaymentRecord(id: 1L,
                type: paymentType,
                createdAt: CURRENT_TIME.minusSeconds(hoursInSystem * 3600),
                cancelled: false)

        when:
        paymentService.cancel(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.of(paymentEntity)
        paymentEntity.cancelled
        paymentEntity.cancellationFee == BigDecimal.valueOf(hoursInSystem) * BigDecimal.valueOf(coefficient)

        where:
        paymentType        | hoursInSystem | coefficient
        EPaymentType.TYPE1 | 2             | 0.05
        EPaymentType.TYPE2 | 3             | 0.1
        EPaymentType.TYPE3 | 4             | 0.15
    }

    def "should throw exception when cancelling already cancelled payment"() {
        given:
        def paymentEntity = new PaymentRecord(id: 1L, cancelled: true)

        when:
        paymentService.cancel(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.of(paymentEntity)
        def ex = thrown(ValidationException)
        ex.exception == EValidationException.PAYMENT_ALREADY_CANCELLED
    }

    def "should throw exception when cancelling non-existent payment"() {
        when:
        paymentService.cancel(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.empty()
        def ex = thrown(ValidationException)
        ex.exception == EValidationException.PAYMENT_DOES_NOT_EXIST
    }

    def "should throw exception when cancelling payment after allowed window"() {
        given:
        def creationTime = CURRENT_TIME.minusDays(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        def paymentEntity = new PaymentRecord(id: 1L,
                type: EPaymentType.TYPE1,
                createdAt: creationTime,
                cancelled: false)

        when:
        paymentService.cancel(1L)

        then:
        1 * paymentRepository.findById(1L) >> Optional.of(paymentEntity)
        def ex = thrown(ValidationException)
        ex.exception == EValidationException.PAYMENT_CANCELLATION_NOT_ALLOWED
    }
}