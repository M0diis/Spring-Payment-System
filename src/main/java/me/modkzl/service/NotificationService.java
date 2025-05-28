package me.modkzl.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.modkzl.enums.EPaymentType;
import me.modkzl.jooq.public_.tables.records.NotificationStatusRecord;
import me.modkzl.repository.notification.ENotificationEndpoint;
import me.modkzl.repository.notification.NotificationRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
// Alternatively can consider using Spring Events
public class NotificationService {

    private final RestTemplate restTemplate;
    private final NotificationRepository notificationRepository;

    @Async
    public void notify(@NotNull EPaymentType paymentType, @NotNull Long paymentId) {
        ENotificationEndpoint url = getNotificationEndpoint(paymentType);

        if (url == null) {
            return;
        }

        try {
            // For demonstration purposes, we will use a mock response
            // ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            ResponseEntity<String> response = getMockResponse(url);

            boolean success = response.getStatusCode().is2xxSuccessful();
            saveNotificationStatus(paymentId, success);
            log.info("Notification for payment {} was {}", paymentId, success ? "successful" : "unsuccessful");
        } catch (Exception e) {
            saveNotificationStatus(paymentId, false);
            log.error("Failed to notify external service for payment {}: {}", paymentId, e.getMessage());
        }
    }

    // Mock method to simulate external service response
    private ResponseEntity<String> getMockResponse(@NotNull ENotificationEndpoint endpoint) {
        return switch (endpoint) {
            case SERVICE_1 -> new ResponseEntity<>("Mock response for SERVICE_1", HttpStatusCode.valueOf(Math.random() < 0.5 ? 200 : 500));
            case SERVICE_2 -> new ResponseEntity<>("Mock response for SERVICE_2", HttpStatusCode.valueOf(Math.random() < 0.5 ? 200 : 500));
            default -> throw new IllegalArgumentException("Unsupported notification endpoint: " + endpoint);
        };
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