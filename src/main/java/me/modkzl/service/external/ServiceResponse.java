package me.modkzl.service.external;

public record ServiceResponse(boolean success, String message) {
    public static ServiceResponse success(String message) {
        return new ServiceResponse(true, message);
    }

    public static ServiceResponse failure(String message) {
        return new ServiceResponse(false, message);
    }
}