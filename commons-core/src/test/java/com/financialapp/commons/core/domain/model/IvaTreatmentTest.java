package com.financialapp.commons.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IvaTreatmentTest {

    @Test
    void exposesExactlyTheThreeArgentineIvaModes() {
        assertThat(IvaTreatment.values())
                .containsExactly(IvaTreatment.INCLUDED, IvaTreatment.SEPARATE, IvaTreatment.EXEMPT);
    }
}
