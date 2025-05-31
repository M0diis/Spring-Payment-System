package me.modkzl.service.external;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.repository.notification.ENotificationEndpoint;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Profile({"!local"})
public class RestExternalServiceClient implements IExternalServiceClient {
    private final RestTemplate restTemplate;

    @Override
    public ServiceResponse get(@NotNull ENotificationEndpoint endpoint) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint.getUrl(), String.class);
            boolean success = response.getStatusCode().is2xxSuccessful();
            return new ServiceResponse(success, response.getBody());
        } catch (Exception e) {
            return ServiceResponse.failure(e.getMessage());
        }
    }
}