package com.project.dividend.exception;

import com.project.dividend.model.constants.ErrorCode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividendException extends RuntimeException {

    private ErrorCode errorCode;
    private int status;
    private String errorMessage;

    public DividendException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
        this.errorMessage = errorCode.getErrorMessage();
    }
}
