package com.project.dividend.exception;

import com.project.dividend.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static com.project.dividend.model.constants.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DividendException.class)
    protected ResponseEntity<ErrorResponse> handleDividendException(DividendException de) {
        log.error("{} is occurred", de.getErrorCode());
        ErrorResponse er = ErrorResponse.builder()
                .errorCode(de.getErrorCode())
                .status(de.getStatus())
                .errorMessage(de.getErrorMessage())
                .build();
        return new ResponseEntity<>(er, Objects.requireNonNull(HttpStatus.resolve(de.getStatus())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ae) {
        log.error("AccessDeniedException is occurred");
        ErrorResponse er = ErrorResponse.builder()
                .errorCode(ACCESS_DENIED)
                .status(ACCESS_DENIED.getStatus())
                .errorMessage(ae.getMessage())
                .build();
        return new ResponseEntity<>(
                er, Objects.requireNonNull(HttpStatus.resolve(
                ACCESS_DENIED.getStatus()
        )));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException be) {
        log.error("{} is occurred", be.getMessage(), be);
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .errorCode(BAD_CREDENTIALS)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorMessage(be.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Exception is occurred ", e);
        ErrorResponse er = ErrorResponse.builder()
                .errorCode(INTERNAL_SERVER_ERROR)
                .status(INTERNAL_SERVER_ERROR.getStatus())
                .errorMessage(INTERNAL_SERVER_ERROR.getErrorMessage())
                .build();
        return new ResponseEntity<>(
                er, Objects.requireNonNull(HttpStatus.resolve(
                INTERNAL_SERVER_ERROR.getStatus()
        )));
    }
}
