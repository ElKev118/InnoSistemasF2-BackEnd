package com.udea.innosistemas.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(int status, String message, long timestamp, Map<String, String> errors) {
        super(status, message, timestamp);
        this.errors = errors;
    }
}