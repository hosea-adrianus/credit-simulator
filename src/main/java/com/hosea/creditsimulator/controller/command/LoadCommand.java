package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.service.LoanApplicationAssembler;
import com.hosea.creditsimulator.service.LoanDataClient;
import com.hosea.creditsimulator.service.LoanSimulationService;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.List;

public class LoadCommand implements Command {

    private final LoanDataClient loanDataClient;
    private final LoanApplicationAssembler assembler;
    private final LoanSimulationService simulationService;
    private final SimulationSession session;
    private final ConsoleView view;

    public LoadCommand(LoanDataClient loanDataClient, LoanApplicationAssembler assembler,
                       LoanSimulationService simulationService, SimulationSession session, ConsoleView view) {
        this.loanDataClient = loanDataClient;
        this.assembler = assembler;
        this.simulationService = simulationService;
        this.session = session;
        this.view = view;
    }

    @Override
    public String name() {
        return "load";
    }

    @Override
    public String description() {
        return "Ambil data perhitungan dari web service lalu hitung otomatis";
    }

    @Override
    public CommandResult execute(List<String> args) {
        view.printInfo("Mengambil data dari web service...");
        try {
            LoanApplication application = assembler.assemble(loanDataClient.fetch());
            LoanSimulation simulation = simulationService.simulate(application);
            session.showNewSimulation(simulation);
            view.printSimulation(simulation);
        } catch (LoanDataException e) {
            view.printError(e.getMessage());
        } catch (ValidationException e) {
            view.printError("Data dari web service tidak valid: " + e.getMessage());
        }
        return CommandResult.CONTINUE;
    }
}
