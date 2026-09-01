package com.financialapp.commons.core.domain.model;

import java.util.regex.Pattern;

/**
 * Argentine CBU (Clave Bancaria Uniforme) / CVU value object: exactly 22 numeric digits.
 */
public record Cbu(String value) {

    private static final Pattern CBU_PATTERN = Pattern.compile("^\\d{22}$");

    /**
     * Sentinel CBU for external/bank-originated transactions (22 zeros).
     */
    public static final Cbu EXTERNAL_INSTALLMENT_CBU = new Cbu("0000000000000000000000");

    public Cbu {
        if (value == null || !CBU_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("CBU must be exactly 22 digits: " + value);
        }
    }

    public static Cbu from(String raw) {
        return new Cbu(raw);
    }

    public String cbuNumber() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
