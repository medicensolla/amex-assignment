package com.asollaorta.amex.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiExceptionResponse {

    private final String message;
    private final HttpStatus status;
    private final ZonedDateTime timeStamp;
}
