package me.modkzl.service.external;

import jakarta.validation.constraints.NotNull;
import me.modkzl.repository.notification.ENotificationEndpoint;

public interface IExternalServiceClient {
    ServiceResponse get(@NotNull ENotificationEndpoint endpoint);
}