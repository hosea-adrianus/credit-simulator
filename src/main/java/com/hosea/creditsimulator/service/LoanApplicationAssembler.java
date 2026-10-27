package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.factory.VehicleFactory;
import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanInput;
import com.hosea.creditsimulator.model.Vehicle;
import com.hosea.creditsimulator.model.enums.VehicleCondition;
import com.hosea.creditsimulator.model.enums.VehicleType;

import java.math.BigDecimal;

/**
 * Validates a complete {@link LoanInput} at once and builds the {@link LoanApplication}.
 * Used when all fields arrive together, e.g. from the web service.
 */
public class LoanApplicationAssembler {

    private final LoanValidator validator;
    private final VehicleFactory vehicleFactory;

    public LoanApplicationAssembler(LoanValidator validator, VehicleFactory vehicleFactory) {
        this.validator = validator;
        this.vehicleFactory = vehicleFactory;
    }

    public LoanApplication assemble(LoanInput input) {
        VehicleType type = validator.parseVehicleType(input.vehicleType());
        VehicleCondition condition = validator.parseVehicleCondition(input.vehicleCondition());
        int year = validator.validateYear(input.vehicleYear(), condition);
        Vehicle vehicle = vehicleFactory.create(type, condition, year);

        BigDecimal loanAmount = validator.validateLoanAmount(input.loanAmount());
        int tenure = validator.validateTenure(input.tenureYears());
        BigDecimal downPayment = validator.validateDownPayment(input.downPayment(), condition, loanAmount);

        return new LoanApplication(vehicle, loanAmount, tenure, downPayment);
    }
}
