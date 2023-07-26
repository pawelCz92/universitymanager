package com.example.universitymanager.domain.common.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }

    public abstract HttpStatus getHttpStatus();
}
