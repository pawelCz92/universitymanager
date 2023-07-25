package com.example.universitymanager.domain.common;

import com.example.universitymanager.domain.common.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponse> handle(BaseException exception, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(exception.getHttpStatus())
                .message(exception.getMessage())
                .request(request)
                .build();
        return createExceptionResponseResponseEntity(exceptionResponse);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Uncaught exception appeared.")
                .request(request)
                .build();
        return createExceptionResponseResponseEntity(exceptionResponse);
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponseResponseEntity(ExceptionResponse exceptionResponse) {
        return ResponseEntity.status(exceptionResponse.getHttpStatusCode()).body(exceptionResponse);
    }

}
