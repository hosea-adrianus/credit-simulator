package com.hosea.creditsimulator.model;

import java.math.BigDecimal;

public final class Motorcycle extends Vehicle {

    private static final BigDecimal BASE_INTEREST_RATE = new BigDecimal("9");

    public Motorcycle(VehicleCondition condition, int year) {
        super(condition, year);
    }

    @Override
    public VehicleType type() {
        return VehicleType.MOTORCYCLE;
    }

    @Override
    public BigDecimal baseInterestRate() {
        return BASE_INTEREST_RATE;
    }
}
