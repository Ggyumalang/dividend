package com.project.dividend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(AbstractException ae){
        log.error(ae.getMessage());
        ErrorResponse er = ErrorResponse.builder()
                                        .code(ae.getStatusCode())
                                        .message(ae.getMessage())
                                        .build();
        return new ResponseEntity<>(er, Objects.requireNonNull(HttpStatus.resolve(ae.getStatusCode())));
    }
}
