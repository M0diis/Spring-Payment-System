package me.modkzl.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.modkzl.enums.EPaymentType;
import me.modkzl.jooq.public_.tables.records.NotificationStatusRecord;
import me.modkzl.repository.notification.ENotificationEndpoint;
import me.modkzl.repository.notification.NotificationRepository;
import me.modkzl.service.external.IExternalServiceClient;
import me.modkzl.service.external.ServiceResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
// Alternatively can consider using Spring Events
public class NotificationService {

    private final IExternalServiceClient externalServiceClient;
    private final NotificationRepository notificationRepository;

    @Async
    public void notify(@NotNull EPaymentType paymentType, @NotNull Long paymentId) {
        ENotificationEndpoint endpoint = getNotificationEndpoint(paymentType);

        if (endpoint == null) {
            return;
        }

        ServiceResponse response = externalServiceClient.get(endpoint);
        saveNotificationStatus(paymentId, response.isSuccess());
        log.info("Notification for payment {} was {}", paymentId, response.isSuccess() ? "successful" : "unsuccessful");
    }

    private ENotificationEndpoint getNotificationEndpoint(@NotNull EPaymentType paymentType) {
        return switch (paymentType) {
            case TYPE1 -> ENotificationEndpoint.SERVICE_1;
            case TYPE2 -> ENotificationEndpoint.SERVICE_2;
            default -> null;
        };
    }

    private void saveNotificationStatus(@NotNull Long paymentId, boolean success) {
        notificationRepository.save(new NotificationStatusRecord()
                .setPaymentId(paymentId)
                .setSuccess(success));
    }
}