package me.modkzl.controllers.payment

import me.modkzl.domain.payment.PaymentDomain
import me.modkzl.dto.PaymentDTO
import me.modkzl.enums.ECurrency
import me.modkzl.enums.EPaymentType
import me.modkzl.mapper.payment.PaymentMapper
import me.modkzl.service.PaymentService
import spock.lang.Specification
import spock.lang.Subject

class PaymentControllerSpec extends Specification {

    def paymentService = Mock(PaymentService)
    def paymentMapper = Mock(PaymentMapper)

    @Subject
    def paymentController = new PaymentController(paymentService, paymentMapper)

    def "should create a payment and return its ID"() {
        given:
        def request = PaymentRequest.builder()
                .paymentType(EPaymentType.TYPE1)
                .amount(BigDecimal.TEN)
                .currency(ECurrency.EUR)
                .debtorIban("DE1234567890")
                .creditorIban("DE0987654321")
                .details("Payment details")
                .build()
        def paymentDTO = Mock(PaymentDTO)
        def paymentId = 1L

        when:
        def response = paymentController.createPayment(request)

        then:
        1 * paymentMapper.mapRequestToDto(request) >> paymentDTO
        1 * paymentService.create(paymentDTO) >> paymentId
        response.paymentId == paymentId
    }

    def "should retrieve a payment by ID"() {
        given:
        def paymentId = 1L
        def paymentDomain = Mock(PaymentDomain)
        def paymentResponse = Mock(PaymentResponse)

        when:
        def response = paymentController.getPayment(paymentId)

        then:
        1 * paymentService.get(paymentId) >> paymentDomain
        1 * paymentMapper.mapDomainToResponse(paymentDomain) >> paymentResponse
        response == paymentResponse
    }

    def "should cancel a payment by ID"() {
        given:
        def paymentId = 1L

        when:
        paymentController.cancelPayment(paymentId)

        then:
        1 * paymentService.cancel(paymentId)
    }

    def "should retrieve a list of payments based on criteria"() {
        given:
        def criteria = PaymentFilterCriteria.builder()
                .amountFrom(BigDecimal.valueOf(100))
                .amountTo(BigDecimal.valueOf(500))
                .build()
        def paymentDomains = [Mock(PaymentDomain), Mock(PaymentDomain)]
        def paymentResponses = [Mock(PaymentResponse), Mock(PaymentResponse)]

        when:
        def response = paymentController.getPayments(criteria)

        then:
        1 * paymentService.get(criteria) >> paymentDomains
        1 * paymentMapper.mapDomainToResponse(paymentDomains[0]) >> paymentResponses[0]
        1 * paymentMapper.mapDomainToResponse(paymentDomains[1]) >> paymentResponses[1]
        response == paymentResponses
    }
}