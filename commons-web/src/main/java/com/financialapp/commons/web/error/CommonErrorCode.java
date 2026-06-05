package com.financialapp.commons.web.error;

import com.financialapp.commons.core.error.ErrorCategory;
import com.financialapp.commons.core.error.ErrorCode;

public enum CommonErrorCode implements ErrorCode {

    VALIDATION_ERROR(ErrorCategory.BAD_REQUEST, "validation_error"),
    MALFORMED_REQUEST(ErrorCategory.BAD_REQUEST, "malformed_request"),
    DATABASE_CONFLICT(ErrorCategory.CONFLICT, "database_conflict"),
    INTERNAL_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "internal_error");

    private final ErrorCategory category;
    private final String code;

    CommonErrorCode(ErrorCategory category, String code) {
        this.category = category;
        this.code = code;
    }

    @Override
    public ErrorCategory category() { return category; }

    @Override
    public String code() { return code; }
}
