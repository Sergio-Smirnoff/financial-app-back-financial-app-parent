package com.financialapp.commons.core.error;

import java.util.Map;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode error;
    private final Map<String, Object> details;

    protected DomainException(ErrorCode error, String message) {
        this(error, message, null);
    }

    protected DomainException(ErrorCode error, String message, Map<String, Object> details) {
        super(message);
        this.error = error;
        this.details = details;
    }

    public ErrorCode getError() { return error; }
    public Map<String, Object> getDetails() { return details; }
}
