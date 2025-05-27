package me.modkzl.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.modkzl.service.IpGeolocationService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private final HttpServletRequest request;
    private final IpGeolocationService ipGeolocationService;

    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void logClientIpAndUrl() {
        String clientIp = request.getRemoteAddr();
        String requestUrl = request.getRequestURL().toString();
        String location = ipGeolocationService.getClientCountry(clientIp);

        log.info("Got request to: {}, Client IP: {}, Location: {}", requestUrl, clientIp, location);
    }
}