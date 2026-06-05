package com.financialapp.commons.core.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void okCarriesStatusTitleAndData() throws Exception {
        String json = mapper.writeValueAsString(ApiResponse.ok(Map.of("id", 1)));
        assertThat(json).contains("\"status\":200").contains("\"title\":\"OK\"").contains("\"id\":1");
        assertThat(json).doesNotContain("\"code\"").doesNotContain("\"success\"").doesNotContain("\"timestamp\"");
    }

    @Test
    void okWithoutMessageOmitsMessage() throws Exception {
        String json = mapper.writeValueAsString(ApiResponse.ok(Map.of("id", 1)));
        assertThat(json).doesNotContain("\"message\"");
    }

    @Test
    void createdUses201AndReasonPhrase() throws Exception {
        String json = mapper.writeValueAsString(ApiResponse.created("Account created", Map.of("id", 1)));
        assertThat(json).contains("\"status\":201").contains("\"title\":\"Created\"")
                .contains("\"message\":\"Account created\"");
    }

    @Test
    void failureCarriesCodeAndDetails() throws Exception {
        String json = mapper.writeValueAsString(
                ApiResponse.failure(HttpStatus.UNPROCESSABLE_ENTITY, "account_insufficient_funds",
                        "Balance too low", Map.of("missing", "150.00")));
        assertThat(json).contains("\"status\":422").contains("\"title\":\"Unprocessable Entity\"")
                .contains("\"code\":\"account_insufficient_funds\"")
                .contains("\"message\":\"Balance too low\"")
                .contains("\"missing\":\"150.00\"");
    }

    @Test
    void failureWithoutDetailsOmitsData() throws Exception {
        String json = mapper.writeValueAsString(
                ApiResponse.failure(HttpStatus.NOT_FOUND, "resource_not_found", "Account not found", null));
        assertThat(json).doesNotContain("\"data\"");
    }
}
