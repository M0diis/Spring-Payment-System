package me.modkzl.repository.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ENotificationEndpoint {
    SERVICE_1("Service 1", "https://service1.example.com/notify"),
    SERVICE_2("Service 2", "https://service2.example.com/notify");

    private final String name;
    private final String url;
}
