package com.hosea.creditsimulator.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A validated loan request.
 *
 * @param vehicle      the financed vehicle
 * @param loanAmount   total loan amount (vehicle price) in rupiah
 * @param tenureYears  loan tenure in years
 * @param downPayment  down payment in rupiah
 */
public record LoanApplication(Vehicle vehicle, BigDecimal loanAmount, int tenureYears, BigDecimal downPayment) {

    public LoanApplication {
        Objects.requireNonNull(vehicle, "vehicle");
        Objects.requireNonNull(loanAmount, "loanAmount");
        Objects.requireNonNull(downPayment, "downPayment");
    }

    /**
     * The amount actually financed: loan amount minus down payment.
     */
    public BigDecimal principal() {
        return loanAmount.subtract(downPayment);
    }
}
