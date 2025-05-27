package me.modkzl.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeProvider {
    public LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }
}
