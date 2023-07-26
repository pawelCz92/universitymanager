package com.example.universitymanager.domain.common;

import com.example.universitymanager.domain.common.exceptions.BaseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handle(IllegalArgumentException exception, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .request(request)
                .build();
        return createExceptionResponseResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handle(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> fieldErrors = exception.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(fieldErrors.toString())
                .request(request)
                .build();
        return createExceptionResponseResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handle(DataIntegrityViolationException exception, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(HttpStatus.CONFLICT)
                .message("Data integrity violation. Request rejected")
                .request(request)
                .build();
        exception.printStackTrace();
        return createExceptionResponseResponseEntity(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handle(Exception exception, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Uncaught exception appeared.")
                .request(request)
                .build();

        exception.printStackTrace();

        return createExceptionResponseResponseEntity(exceptionResponse);
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponseResponseEntity(ExceptionResponse exceptionResponse) {
        return ResponseEntity.status(exceptionResponse.getHttpStatusCode()).body(exceptionResponse);
    }

}
