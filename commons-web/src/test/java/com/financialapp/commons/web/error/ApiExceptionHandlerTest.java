package com.financialapp.commons.web.error;

import com.financialapp.commons.core.error.DomainException;
import com.financialapp.commons.core.error.ErrorCategory;
import com.financialapp.commons.core.error.ErrorCode;
import com.financialapp.commons.core.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private enum TestError implements ErrorCode {
        BROKEN;
        @Override public ErrorCategory category() { return ErrorCategory.UNPROCESSABLE; }
        @Override public String code() { return "broken"; }
    }

    private static final class TestDomainException extends DomainException {
        TestDomainException() { super(TestError.BROKEN, "broke", Map.of("why", "reasons")); }
    }

    private static final class TestHandler extends ApiExceptionHandler {
        @Override
        protected Map<String, String> constraintMessages() {
            return Map.of("uq_test", "Already exists");
        }
    }

    private final TestHandler handler = new TestHandler();

    @Test
    void domainExceptionMapsCategoryToStatusAndCarriesCode() {
        ResponseEntity<ApiResponse<Map<String, Object>>> response =
                handler.handleDomain(new TestDomainException());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().getCode()).isEqualTo("broken");
        assertThat(response.getBody().getMessage()).isEqualTo("broke");
        assertThat(response.getBody().getData()).containsEntry("why", "reasons");
    }

    @Test
    void dataIntegrityUsesConstraintMessageHook() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("boom: violates uq_test constraint");
        ResponseEntity<ApiResponse<Map<String, Object>>> response = handler.handleDataIntegrity(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getCode()).isEqualTo("database_conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Already exists");
        assertThat(response.getBody().getData()).containsEntry("constraint", "uq_test");
    }

    @Test
    void unknownConstraintFallsBackToGenericMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("other failure");
        ResponseEntity<ApiResponse<Map<String, Object>>> response = handler.handleDataIntegrity(ex);
        assertThat(response.getBody().getMessage()).isEqualTo("Data conflict");
    }

    @Test
    void genericExceptionIs500InternalError() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleGeneric(new RuntimeException("x"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo("internal_error");
    }

    @Test
    void notNullViolationMapsTo400WithColumn() {
        var cause = new java.sql.SQLException(
            "ERROR: null value in column \"alias\" of relation \"accounts\" violates not-null constraint",
            "23502");
        var ex = new DataIntegrityViolationException("wrapper", cause);

        var response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("validation_error");
        assertThat(response.getBody().getData()).containsEntry("column", "alias");
    }

    @Test
    void uniqueViolationStillMapsTo409() {
        var cause = new java.sql.SQLException("duplicate key value violates unique constraint", "23505");
        var ex = new DataIntegrityViolationException("wrapper", cause);

        var response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getCode()).isEqualTo("database_conflict");
    }
}
