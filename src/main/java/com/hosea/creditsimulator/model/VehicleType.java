package com.hosea.creditsimulator.model;

import com.hosea.creditsimulator.exception.ValidationException;

import java.util.Arrays;

public enum VehicleType {
    CAR("Mobil"),
    MOTORCYCLE("Motor");

    private final String label;

    VehicleType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static VehicleType fromLabel(String input) {
        String value = input == null ? "" : input.trim();
        return Arrays.stream(values())
                .filter(type -> type.label.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        "Jenis kendaraan harus Motor atau Mobil (input: '" + value + "')"));
    }
}
