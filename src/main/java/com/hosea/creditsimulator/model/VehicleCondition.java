package com.hosea.creditsimulator.model;

import com.hosea.creditsimulator.exception.ValidationException;

import java.math.BigDecimal;
import java.util.Arrays;

public enum VehicleCondition {
    NEW("Baru", new BigDecimal("0.35")),
    USED("Bekas", new BigDecimal("0.25"));

    private final String label;
    private final BigDecimal minDownPaymentRatio;

    VehicleCondition(String label, BigDecimal minDownPaymentRatio) {
        this.label = label;
        this.minDownPaymentRatio = minDownPaymentRatio;
    }

    public String label() {
        return label;
    }

    /**
     * Minimum down payment as a fraction of the total loan, e.g. 0.35 for 35%.
     */
    public BigDecimal minDownPaymentRatio() {
        return minDownPaymentRatio;
    }

    public static VehicleCondition fromLabel(String input) {
        String value = input == null ? "" : input.trim();
        return Arrays.stream(values())
                .filter(condition -> condition.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        "Kondisi kendaraan harus Baru atau Bekas (input: '" + value + "')"));
    }
}
