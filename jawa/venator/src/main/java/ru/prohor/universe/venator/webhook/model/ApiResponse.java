package ru.prohor.universe.venator.webhook.model;

public record ApiResponse(
        boolean success,
        String message
) {
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }
}
