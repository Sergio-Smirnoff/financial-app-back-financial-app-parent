package com.financialapp.commons.core.error;

import java.util.Map;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode error;
    private final Map<String, Object> details;

    protected DomainException(ErrorCode error, String message) {
        super(message);
        this.error = error;
        this.details = null;
    }

    protected DomainException(ErrorCode error, String message, Map<String, Object> details) {
        super(message);
        this.error = error;
        this.details = details;
    }

    protected DomainException(ErrorCode error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.details = null;
    }

    public ErrorCode getError() { return error; }
    public Map<String, Object> getDetails() { return details; }
}
