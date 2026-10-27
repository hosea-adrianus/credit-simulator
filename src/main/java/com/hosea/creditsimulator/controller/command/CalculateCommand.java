package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.controller.InputPrompter;
import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.factory.VehicleFactory;
import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.model.Vehicle;
import com.hosea.creditsimulator.model.VehicleCondition;
import com.hosea.creditsimulator.model.VehicleType;
import com.hosea.creditsimulator.service.LoanSimulationService;
import com.hosea.creditsimulator.service.LoanValidator;
import com.hosea.creditsimulator.view.ConsoleView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CalculateCommand implements Command {

    private final InputPrompter prompter;
    private final LoanValidator validator;
    private final VehicleFactory vehicleFactory;
    private final LoanSimulationService simulationService;
    private final SimulationSession session;
    private final ConsoleView view;

    public CalculateCommand(InputPrompter prompter, LoanValidator validator, VehicleFactory vehicleFactory,
                            LoanSimulationService simulationService, SimulationSession session, ConsoleView view) {
        this.prompter = prompter;
        this.validator = validator;
        this.vehicleFactory = vehicleFactory;
        this.simulationService = simulationService;
        this.session = session;
        this.view = view;
    }

    @Override
    public String name() {
        return "calculate";
    }

    @Override
    public String description() {
        return "Hitung cicilan baru (input data kendaraan & pinjaman)";
    }

    @Override
    public CommandResult execute(List<String> args) {
        Optional<LoanApplication> application = askLoanApplication();
        if (application.isEmpty()) {
            view.printError("Input berakhir sebelum semua data diisi, perhitungan dibatalkan");
            return CommandResult.CONTINUE;
        }
        LoanSimulation simulation = simulationService.simulate(application.get());
        session.showNewSimulation(simulation);
        view.printSimulation(simulation);
        return CommandResult.CONTINUE;
    }

    private Optional<LoanApplication> askLoanApplication() {
        Optional<VehicleType> type = prompter.ask("Jenis Kendaraan (Motor|Mobil)", validator::parseVehicleType);
        if (type.isEmpty()) {
            return Optional.empty();
        }
        Optional<VehicleCondition> condition =
                prompter.ask("Kondisi Kendaraan (Baru|Bekas)", validator::parseVehicleCondition);
        if (condition.isEmpty()) {
            return Optional.empty();
        }
        Optional<Integer> year = prompter.ask("Tahun Kendaraan (4 digit)",
                raw -> validator.validateYear(raw, condition.get()));
        if (year.isEmpty()) {
            return Optional.empty();
        }
        Optional<BigDecimal> loanAmount =
                prompter.ask("Jumlah Pinjaman Total (maks 1000000000)", validator::validateLoanAmount);
        if (loanAmount.isEmpty()) {
            return Optional.empty();
        }
        Optional<Integer> tenure = prompter.ask("Tenor Pinjaman (1-6 thn)", validator::validateTenure);
        if (tenure.isEmpty()) {
            return Optional.empty();
        }
        Optional<BigDecimal> downPayment = prompter.ask("Jumlah DP",
                raw -> validator.validateDownPayment(raw, condition.get(), loanAmount.get()));
        if (downPayment.isEmpty()) {
            return Optional.empty();
        }

        Vehicle vehicle = vehicleFactory.create(type.get(), condition.get(), year.get());
        return Optional.of(new LoanApplication(vehicle, loanAmount.get(), tenure.get(), downPayment.get()));
    }
}
