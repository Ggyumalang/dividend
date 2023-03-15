package com.project.dividend.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."),
    BAD_CREDENTIALS(HttpStatus.BAD_REQUEST.value(), "자격 증명에 실패하였습니다."),
    INVALID_MONTH(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 월입니다."),
    TICKER_IS_EMPTY(HttpStatus.BAD_REQUEST.value(), "TICKER가 비어있습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    USER_ID_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 ID입니다."),
    SCRAPING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "스크래핑 작업이 실패하였습니다."),
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 회사명 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "내부 서버 오류입니다."),
    INVALID_TICKER(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 TICKER입니다."),
    ALREADY_EXIST_TICKER(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 TICKER입니다."),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 사용자ID 입니다.");

    private final int status;
    private final String errorMessage;
}
