package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanSimulation;

/**
 * Turns a validated {@link LoanApplication} into a {@link LoanSimulation}.
 */
public class LoanSimulationService {

    private final InstallmentCalculator installmentCalculator;

    public LoanSimulationService(InstallmentCalculator installmentCalculator) {
        this.installmentCalculator = installmentCalculator;
    }

    public LoanSimulation simulate(LoanApplication application) {
        return new LoanSimulation(application, installmentCalculator.calculate(
                application.principal(),
                application.vehicle().baseInterestRate(),
                application.tenureYears()));
    }
}
