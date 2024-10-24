package com.asollaorta.amex.api.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponse> handleApiException(ApiException ex) {
        ApiExceptionResponse response = new ApiExceptionResponse(
                ex.getMessage(),
                ex.getHttpStatus(),
                ex.getTimeStamp()
        );
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

}
