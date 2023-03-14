package com.project.dividend.model;

import com.project.dividend.model.constants.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private ErrorCode errorCode;
    private int status;
    private String errorMessage;
}
