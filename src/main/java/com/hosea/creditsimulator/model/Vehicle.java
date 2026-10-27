package com.hosea.creditsimulator.model;

import com.hosea.creditsimulator.model.enums.VehicleCondition;
import com.hosea.creditsimulator.model.enums.VehicleType;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Vehicle {

    private final VehicleCondition condition;
    private final int year;

    protected Vehicle(VehicleCondition condition, int year) {
        this.condition = Objects.requireNonNull(condition, "condition");
        this.year = year;
    }

    public abstract VehicleType type();

    /**
     * Base yearly interest rate in percent (e.g. 8 for 8%) applied in the first year.
     */
    public abstract BigDecimal baseInterestRate();

    public VehicleCondition condition() {
        return condition;
    }

    public int year() {
        return year;
    }

    public BigDecimal minDownPaymentRatio() {
        return condition.minDownPaymentRatio();
    }

    @Override
    public String toString() {
        return type().label() + " " + condition.label() + " (" + year + ")";
    }
}
