package com.hosea.creditsimulator.repository;

import com.hosea.creditsimulator.model.Car;
import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.model.VehicleCondition;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemorySheetRepositoryTest {

    private final SheetRepository repository = new InMemorySheetRepository();

    @Test
    void savesAndFindsSheets() {
        LoanSimulation simulation = simulation(3);

        assertFalse(repository.save("avanza", simulation));

        assertSame(simulation, repository.find("avanza").orElseThrow());
        assertEquals(Optional.empty(), repository.find("unknown"));
    }

    @Test
    void savingSameNameReplacesSheet() {
        repository.save("avanza", simulation(3));
        LoanSimulation replacement = simulation(5);

        assertTrue(repository.save("avanza", replacement));
        assertSame(replacement, repository.find("avanza").orElseThrow());
        assertEquals(List.of("avanza"), repository.names());
    }

    @Test
    void namesKeepInsertionOrder() {
        repository.save("b", simulation(1));
        repository.save("a", simulation(2));
        repository.save("c", simulation(3));

        assertEquals(List.of("b", "a", "c"), repository.names());
    }

    private static LoanSimulation simulation(int tenure) {
        LoanApplication application = new LoanApplication(new Car(VehicleCondition.NEW, 2025),
                new BigDecimal("100000000"), tenure, new BigDecimal("35000000"));
        return new LoanSimulation(application, List.of());
    }
}
