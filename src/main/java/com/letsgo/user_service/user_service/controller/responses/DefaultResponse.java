package com.letsgo.user_service.user_service.controller.responses;

import org.springframework.http.HttpStatus;

// Default response for app
public class DefaultResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    // Constructor with status and message only
    public DefaultResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.data = null; // Data is null by default in this case
    }

    // Constructor with data
    public DefaultResponse(T data) {
        this.status = HttpStatus.OK.value(); // Default to 200 OK status
        this.message = "Success";
        this.data = data;
    }

    // Constructor with status, message, and data
    public DefaultResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
