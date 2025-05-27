package me.modkzl.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpGeolocationService {
    private static final String GEOLOCATION_API_URL = "http://ip-api.com/json/";
    private static final String PUBLIC_IP_API_URL = "https://api.ipify.org";

    private final Environment environment;
    private final RestTemplate restTemplate;

    public @Nullable String getClientCountry(@Nullable String clientIp) {
        if (clientIp == null || clientIp.isEmpty()) {
            log.warn("Client IP is null or empty, cannot resolve country.");
            return null;
        }

        // ---
        // For local testing, can be removed.
        if (isLocalEnvironment()) {
            clientIp = getPublicIp();
        }
        // ---

        try {
            String url = GEOLOCATION_API_URL + clientIp;
            GeolocationResponse response = restTemplate.getForObject(url, GeolocationResponse.class);

            if (response != null && "success".equalsIgnoreCase(response.status())) {
                return response.country();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error while resolving country for IP: {}", clientIp, e);
        }
        return null;
    }

    private String getPublicIp() {
        try {
            return restTemplate.getForObject(PUBLIC_IP_API_URL, String.class);
        } catch (Exception e) {
            log.error("Error while fetching public IP.", e);
            return null;
        }
    }

    private boolean isLocalEnvironment() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("local"));
    }

    private record GeolocationResponse(String status, String country) {
    }
}