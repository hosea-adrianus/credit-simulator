package com.hosea.creditsimulator.controller;

import com.hosea.creditsimulator.model.LoanSimulation;

import java.util.Optional;

/**
 * State shared by the commands during one run: the simulation currently on screen
 * and the sheet it belongs to, if it was saved or switched to.
 */
public class SimulationSession {

    private LoanSimulation activeSimulation;
    private String activeSheet;

    public Optional<LoanSimulation> activeSimulation() {
        return Optional.ofNullable(activeSimulation);
    }

    public Optional<String> activeSheet() {
        return Optional.ofNullable(activeSheet);
    }

    public void showNewSimulation(LoanSimulation simulation) {
        this.activeSimulation = simulation;
        this.activeSheet = null;
    }

    public void activateSheet(String sheetName, LoanSimulation simulation) {
        this.activeSimulation = simulation;
        this.activeSheet = sheetName;
    }
}
