package com.hosea.creditsimulator.repository;

import com.hosea.creditsimulator.model.LoanSimulation;

import java.util.List;
import java.util.Optional;

public interface SheetRepository {

    /**
     * Saves the simulation under the given name, replacing any sheet with that name.
     *
     * @return {@code true} if an existing sheet was replaced
     */
    boolean save(String name, LoanSimulation simulation);

    Optional<LoanSimulation> find(String name);

    /**
     * Sheet names in the order they were first saved.
     */
    List<String> names();
}
