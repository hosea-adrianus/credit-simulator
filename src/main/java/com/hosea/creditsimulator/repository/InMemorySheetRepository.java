package com.hosea.creditsimulator.repository;

import com.hosea.creditsimulator.model.LoanSimulation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Keeps sheets in memory for the lifetime of the application.
 */
public class InMemorySheetRepository implements SheetRepository {

    private final Map<String, LoanSimulation> sheets = new LinkedHashMap<>();

    @Override
    public boolean save(String name, LoanSimulation simulation) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(simulation, "simulation");
        return sheets.put(name, simulation) != null;
    }

    @Override
    public Optional<LoanSimulation> find(String name) {
        return Optional.ofNullable(sheets.get(name));
    }

    @Override
    public List<String> names() {
        return List.copyOf(sheets.keySet());
    }
}
