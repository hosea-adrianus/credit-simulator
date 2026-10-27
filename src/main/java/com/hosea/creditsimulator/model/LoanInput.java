package com.hosea.creditsimulator.model;

/**
 * Raw, not yet validated loan fields as they arrive from a user or a web service.
 */
public record LoanInput(
        String vehicleType,
        String vehicleCondition,
        String vehicleYear,
        String loanAmount,
        String tenureYears,
        String downPayment) {
}
