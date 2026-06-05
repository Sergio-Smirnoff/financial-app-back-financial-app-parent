package com.financialapp.commons.web.error;

import com.financialapp.commons.core.error.ErrorCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCategoryHttpMapperTest {

    @ParameterizedTest
    @EnumSource(ErrorCategory.class)
    void everyCategoryMaps(ErrorCategory category) {
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(category)).isNotNull();
    }

    @Test
    void mappingIsTheSpecTable() {
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.BAD_REQUEST)).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.UNAUTHORIZED)).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.FORBIDDEN)).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.NOT_FOUND)).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.CONFLICT)).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.UNPROCESSABLE)).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.TOO_MANY_REQUESTS)).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(ErrorCategoryHttpMapper.toHttpStatus(ErrorCategory.INTERNAL_SERVER_ERROR)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void commonErrorCodesExposeGenericSlugs() {
        assertThat(CommonErrorCode.VALIDATION_ERROR.code()).isEqualTo("validation_error");
        assertThat(CommonErrorCode.MALFORMED_REQUEST.code()).isEqualTo("malformed_request");
        assertThat(CommonErrorCode.DATABASE_CONFLICT.code()).isEqualTo("database_conflict");
        assertThat(CommonErrorCode.INTERNAL_ERROR.code()).isEqualTo("internal_error");
    }
}
