package com.financialapp.commons.core.error;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    private enum TestError implements ErrorCode {
        SOMETHING_BROKEN;

        @Override
        public ErrorCategory category() { return ErrorCategory.UNPROCESSABLE; }

        @Override
        public String code() { return "something_broken"; }
    }

    private static final class TestException extends DomainException {
        TestException() { super(TestError.SOMETHING_BROKEN, "it broke", Map.of("k", "v")); }
    }

    @Test
    void carriesErrorCodeMessageAndDetails() {
        TestException ex = new TestException();
        assertThat(ex.getError().code()).isEqualTo("something_broken");
        assertThat(ex.getError().category()).isEqualTo(ErrorCategory.UNPROCESSABLE);
        assertThat(ex.getMessage()).isEqualTo("it broke");
        assertThat(ex.getDetails()).containsEntry("k", "v");
    }

    @Test
    void detailsAreOptional() {
        DomainException ex = new DomainException(TestError.SOMETHING_BROKEN, "msg") {};
        assertThat(ex.getDetails()).isNull();
    }

    @Test
    void categoryEnumCoversAllHttpErrorFamiliesUsed() {
        assertThat(ErrorCategory.values()).containsExactly(
                ErrorCategory.BAD_REQUEST, ErrorCategory.UNAUTHORIZED, ErrorCategory.FORBIDDEN,
                ErrorCategory.NOT_FOUND, ErrorCategory.CONFLICT, ErrorCategory.UNPROCESSABLE,
                ErrorCategory.TOO_MANY_REQUESTS, ErrorCategory.INTERNAL_SERVER_ERROR);
    }
}
