package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.factory.VehicleFactory;
import com.hosea.creditsimulator.model.Car;
import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanInput;
import com.hosea.creditsimulator.model.LoanSimulation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanSimulationServiceTest {

    private final LoanApplicationAssembler assembler = new LoanApplicationAssembler(
            new LoanValidator(Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC)),
            new VehicleFactory());
    private final LoanSimulationService service =
            new LoanSimulationService(new InstallmentCalculator(new InterestRateCalculator()));

    @Test
    void assemblesAndSimulatesCompleteInput() {
        LoanApplication application = assembler.assemble(
                new LoanInput("mobil", "BARU", "2025", "100000000", "3", "35000000"));

        assertInstanceOf(Car.class, application.vehicle());
        assertEquals(new BigDecimal("65000000"), application.principal());

        LoanSimulation simulation = service.simulate(application);
        assertEquals(3, simulation.installments().size());
        assertEquals(new BigDecimal("1950000.00"), simulation.installments().get(0).monthlyInstallment());
    }

    @Test
    void assemblerAppliesTheSameRulesAsManualInput() {
        assertThrows(ValidationException.class, () -> assembler.assemble(
                new LoanInput("Mobil", "Baru", "2025", "100000000", "3", "10000000")));
        assertThrows(ValidationException.class, () -> assembler.assemble(
                new LoanInput("Motor", "Bekas", "2015", "100000000", "7", "30000000")));
        assertThrows(ValidationException.class, () -> assembler.assemble(
                new LoanInput(null, "Baru", "2025", "100000000", "3", "35000000")));
    }
}
