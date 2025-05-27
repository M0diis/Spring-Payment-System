package me.modkzl.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.modkzl.enums.EPaymentType;
import me.modkzl.jooq.public_.tables.records.NotificationStatusRecord;
import me.modkzl.repository.notification.ENotificationEndpoint;
import me.modkzl.repository.notification.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final RestTemplate restTemplate;
    private final NotificationRepository notificationRepository;

    @Async
    public void notify(@NotNull EPaymentType paymentType, @NotNull Long paymentId) {
        String url = getNotificationUrl(paymentType);

        if (url == null) {
            return;
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            boolean success = response.getStatusCode().is2xxSuccessful();
            saveNotificationStatus(paymentId, success);
            log.info("Notification for payment {} was {}", paymentId, success ? "successful" : "unsuccessful");
        } catch (Exception e) {
            saveNotificationStatus(paymentId, false);
            log.error("Failed to notify external service for payment {}: {}", paymentId, e.getMessage());
        }
    }

    private String getNotificationUrl(@NotNull EPaymentType paymentType) {
        return switch (paymentType) {
            case TYPE1 -> ENotificationEndpoint.SERVICE_1.getUrl();
            case TYPE2 -> ENotificationEndpoint.SERVICE_2.getUrl();
            default -> null;
        };
    }

    private void saveNotificationStatus(@NotNull Long paymentId, boolean success) {
        notificationRepository.save(new NotificationStatusRecord()
                .setPaymentId(paymentId)
                .setSuccess(success));
    }
}