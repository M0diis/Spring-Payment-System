package me.modkzl.service

import me.modkzl.enums.EPaymentType
import me.modkzl.jooq.public_.tables.records.NotificationStatusRecord
import me.modkzl.repository.notification.ENotificationEndpoint
import me.modkzl.repository.notification.NotificationRepository
import me.modkzl.service.external.IExternalServiceClient
import me.modkzl.service.external.ServiceResponse
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class NotificationServiceSpec extends Specification {

    def externalServiceClient = Mock(IExternalServiceClient)
    def notificationRepository = Mock(NotificationRepository)

    @Subject
    def notificationService = new NotificationService(externalServiceClient, notificationRepository)

    @Unroll
    def "should notify external service for payment type #paymentType and save status"() {
        given:
        externalServiceClient.get(_ as ENotificationEndpoint) >> new ServiceResponse(success, "Mock response")

        when:
        notificationService.notify(paymentType, paymentId)

        then:
        1 * notificationRepository.save({ NotificationStatusRecord it ->
            it.paymentId == paymentId && it.success == success
        })

        where:
        paymentType        | paymentId | success
        EPaymentType.TYPE1 | 1L        | true
        EPaymentType.TYPE1 | 2L        | false
        EPaymentType.TYPE2 | 3L        | true
        EPaymentType.TYPE2 | 4L        | false
    }

    def "should handle unsupported payment type gracefully"() {
        when:
        notificationService.notify(EPaymentType.TYPE3, 5L)

        then:
        0 * externalServiceClient.get(_)
        0 * notificationRepository.save(_)
    }

    def "should log error when external service throws exception"() {
        given:
        externalServiceClient.get(_ as ENotificationEndpoint) >> ServiceResponse.failure("Service error")

        when:
        notificationService.notify(EPaymentType.TYPE1, 6L)

        then:
        1 * notificationRepository.save({ NotificationStatusRecord it ->
            it.paymentId == 6L && !it.success
        })
    }
}