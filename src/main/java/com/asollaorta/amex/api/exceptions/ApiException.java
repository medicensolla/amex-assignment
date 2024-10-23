package com.asollaorta.amex.api.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ZonedDateTime timeStamp;

    public ApiException(String message, HttpStatus httpStatus, ZonedDateTime timeStamp) {
        super(message);
        this.httpStatus = httpStatus;
        this.timeStamp = timeStamp;
    }


}
