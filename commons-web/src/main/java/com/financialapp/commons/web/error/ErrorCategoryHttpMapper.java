package com.financialapp.commons.web.error;

import com.financialapp.commons.core.error.ErrorCategory;
import org.springframework.http.HttpStatus;

public final class ErrorCategoryHttpMapper {

    private ErrorCategoryHttpMapper() {}

    public static HttpStatus toHttpStatus(ErrorCategory category) {
        return switch (category) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNPROCESSABLE -> HttpStatus.UNPROCESSABLE_ENTITY;
            case TOO_MANY_REQUESTS -> HttpStatus.TOO_MANY_REQUESTS;
            case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
