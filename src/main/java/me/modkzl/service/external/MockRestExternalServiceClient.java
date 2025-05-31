package me.modkzl.service.external;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.modkzl.repository.notification.ENotificationEndpoint;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"local"})
public class MockRestExternalServiceClient implements IExternalServiceClient {
    @Override
    public ServiceResponse get(@NotNull ENotificationEndpoint endpoint) {
        return switch (endpoint) {
            case SERVICE_1 -> ServiceResponse.success("Mock response for SERVICE_1");
            case SERVICE_2 -> ServiceResponse.failure("Mock response for SERVICE_2");
            default -> throw new IllegalArgumentException("Unsupported notification endpoint: " + endpoint);
        };
    }
}