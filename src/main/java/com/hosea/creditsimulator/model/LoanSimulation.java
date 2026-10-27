package com.hosea.creditsimulator.model;

import java.util.List;
import java.util.Objects;

/**
 * The result of simulating a loan: the application and its installment for every year.
 */
public record LoanSimulation(LoanApplication application, List<YearlyInstallment> installments) {

    public LoanSimulation {
        Objects.requireNonNull(application, "application");
        installments = List.copyOf(installments);
    }
}
