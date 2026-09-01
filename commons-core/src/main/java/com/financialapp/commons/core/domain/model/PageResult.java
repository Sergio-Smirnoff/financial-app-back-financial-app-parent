package com.financialapp.commons.core.domain.model;

import java.util.List;

public record PageResult<T>(List<T> content, boolean hasNext, String nextCursor, long totalElements) {

    public PageResult {
        content = content == null ? List.of() : List.copyOf(content);
        if (hasNext && (nextCursor == null || nextCursor.isBlank())) {
            throw new IllegalArgumentException("nextCursor is required when hasNext is true");
        }
        if (!hasNext && nextCursor != null) {
            throw new IllegalArgumentException("nextCursor must be null when hasNext is false");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must not be negative");
        }
    }
}
