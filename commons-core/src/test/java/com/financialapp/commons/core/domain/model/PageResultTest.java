package com.financialapp.commons.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PageResultTest {

    @Test
    void copiesContentDefensivelyAndAcceptsNullAsEmpty() {
        List<String> mutable = new ArrayList<>(List.of("a"));
        PageResult<String> page = new PageResult<>(mutable, false, null, 1);
        mutable.add("b");
        assertThat(page.content()).containsExactly("a");
        assertThat(new PageResult<String>(null, false, null, 0).content()).isEmpty();
    }

    @Test
    void hasNextRequiresCursor() {
        assertThatThrownBy(() -> new PageResult<>(List.of("a"), true, null, 10))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PageResult<>(List.of("a"), true, "  ", 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void lastPageForbidsCursor() {
        assertThatThrownBy(() -> new PageResult<>(List.of("a"), false, "cursor", 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNegativeTotal() {
        assertThatThrownBy(() -> new PageResult<>(List.of(), false, null, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
