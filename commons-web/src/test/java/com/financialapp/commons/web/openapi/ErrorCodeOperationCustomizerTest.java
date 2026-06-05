package com.financialapp.commons.web.openapi;

import com.financialapp.commons.core.error.ErrorCategory;
import com.financialapp.commons.core.error.ErrorCode;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorCodeOperationCustomizerTest {

    enum TestCatalog implements ErrorCode {
        NOT_FOUND_THING(ErrorCategory.NOT_FOUND, "thing_not_found"),
        BROKEN_RULE(ErrorCategory.UNPROCESSABLE, "broken_rule");

        private final ErrorCategory category;
        private final String code;

        TestCatalog(ErrorCategory category, String code) {
            this.category = category;
            this.code = code;
        }

        @Override public ErrorCategory category() { return category; }
        @Override public String code() { return code; }
    }

    static class Controller {
        @ApiErrorCodes(catalog = TestCatalog.class, value = {"thing_not_found", "broken_rule"})
        public void annotated() {}

        public void bare() {}

        @ApiErrorCodes(catalog = TestCatalog.class, value = {"no_such_slug"})
        public void wrong() {}
    }

    private final ErrorCodeOperationCustomizer customizer = new ErrorCodeOperationCustomizer();

    private HandlerMethod handler(String method) throws Exception {
        return new HandlerMethod(new Controller(), Controller.class.getMethod(method));
    }

    @Test
    void annotatedEndpointGetsDeclaredAndGenericResponses() throws Exception {
        Operation operation = new Operation().responses(new ApiResponses());
        customizer.customize(operation, handler("annotated"));
        ApiResponses responses = operation.getResponses();
        assertThat(responses.keySet()).contains("404", "422", "400", "409", "500");
        String example404 = responses.get("404").getContent()
                .get("application/json").getExamples().get("thing_not_found").getValue().toString();
        assertThat(example404).contains("\"code\": \"thing_not_found\"").contains("\"status\": 404");
    }

    @Test
    void bareEndpointStillGetsGenericResponses() throws Exception {
        Operation operation = new Operation().responses(new ApiResponses());
        customizer.customize(operation, handler("bare"));
        assertThat(operation.getResponses().keySet()).contains("400", "409", "500").doesNotContain("404");
    }

    @Test
    void unknownSlugFailsFast() throws Exception {
        Operation operation = new Operation().responses(new ApiResponses());
        assertThatThrownBy(() -> customizer.customize(operation, handler("wrong")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no_such_slug");
    }
}
