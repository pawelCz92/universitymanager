package com.example.universitymanager.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@NoArgsConstructor
@Getter
public class ExceptionResponse {

    @Builder
    public ExceptionResponse(HttpStatus httpStatus, String message, HttpServletRequest request) {
        this.httpStatusCode = httpStatus.value();
        this.message = message;
        this.method = request.getMethod();
        this.requestUri = request.getRequestURI();
    }

    private int httpStatusCode;
    private String method;
    private String requestUri;
    private String message;
}