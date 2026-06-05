package com.financialapp.commons.web.openapi;

import com.financialapp.commons.core.error.ErrorCode;
import com.financialapp.commons.web.error.CommonErrorCode;
import com.financialapp.commons.web.error.ErrorCategoryHttpMapper;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ErrorCodeOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        List<ErrorCode> codes = new ArrayList<>(declaredCodes(handlerMethod));
        codes.addAll(Arrays.asList(CommonErrorCode.values()));

        Map<HttpStatus, List<ErrorCode>> byStatus = new LinkedHashMap<>();
        for (ErrorCode code : codes) {
            byStatus.computeIfAbsent(
                    ErrorCategoryHttpMapper.toHttpStatus(code.category()), status -> new ArrayList<>()).add(code);
        }

        ApiResponses responses = operation.getResponses() != null ? operation.getResponses() : new ApiResponses();
        byStatus.forEach((status, statusCodes) ->
                responses.addApiResponse(String.valueOf(status.value()), buildResponse(status, statusCodes)));
        operation.setResponses(responses);
        return operation;
    }

    private List<ErrorCode> declaredCodes(HandlerMethod handlerMethod) {
        ApiErrorCodes annotation = handlerMethod.getMethodAnnotation(ApiErrorCodes.class);
        if (annotation == null) {
            return List.of();
        }
        ErrorCode[] catalog = annotation.catalog().getEnumConstants();
        if (catalog == null) {
            throw new IllegalStateException(
                    "@ApiErrorCodes catalog must be an enum: " + annotation.catalog().getName());
        }
        List<ErrorCode> resolved = new ArrayList<>();
        for (String slug : annotation.value()) {
            resolved.add(Arrays.stream(catalog)
                    .filter(candidate -> candidate.code().equals(slug))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Unknown error code '" + slug + "' in catalog " + annotation.catalog().getName())));
        }
        return resolved;
    }

    private io.swagger.v3.oas.models.responses.ApiResponse buildResponse(HttpStatus status, List<ErrorCode> codes) {
        MediaType mediaType = new MediaType();
        for (ErrorCode code : codes) {
            mediaType.addExamples(code.code(), new Example().value("""
                    {
                      "status": %d,
                      "title": "%s",
                      "code": "%s",
                      "message": "<human readable message>"
                    }""".formatted(status.value(), status.getReasonPhrase(), code.code())));
        }
        return new io.swagger.v3.oas.models.responses.ApiResponse()
                .description(status.getReasonPhrase())
                .content(new Content().addMediaType("application/json", mediaType));
    }
}
