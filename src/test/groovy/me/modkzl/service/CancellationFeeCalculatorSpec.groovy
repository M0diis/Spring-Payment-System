package me.modkzl.service

import me.modkzl.enums.EPaymentType
import me.modkzl.exception.EValidationException
import me.modkzl.exception.ValidationException
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CancellationFeeCalculatorSpec extends Specification {

    @Subject
    def cancellationFeeCalculator = new CancellationFeeCalculator()

    @Unroll
    def "should calculate cancellation fee for payment type #paymentType"() {
        when: "calculate is called with factor #factor"
        def result = cancellationFeeCalculator.calculate(paymentType, factor)

        then: "the cancellation fee is calculated correctly"
        result == expectedFee

        where:
        paymentType        | factor | expectedFee
        EPaymentType.TYPE1 | 10.0   | 0.5
        EPaymentType.TYPE2 | 10.0   | 1.0
        EPaymentType.TYPE3 | 10.0   | 1.5
    }

    def "should throw exception for unsupported payment type"() {
        when: "calculate is called"
        cancellationFeeCalculator.calculate(EPaymentType.TYPE4, 10.0)

        then: "an exception is thrown"
        def e = thrown(ValidationException)
        e.getException() == EValidationException.PAYMENT_TYPE_NOT_SUPPORTED
    }
}