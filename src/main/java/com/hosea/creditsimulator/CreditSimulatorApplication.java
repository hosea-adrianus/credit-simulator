package com.hosea.creditsimulator;

import com.hosea.creditsimulator.client.LoanDataClient;
import com.hosea.creditsimulator.config.AppConfig;
import com.hosea.creditsimulator.controller.CreditSimulatorController;
import com.hosea.creditsimulator.controller.InputPrompter;
import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.controller.command.CalculateCommand;
import com.hosea.creditsimulator.controller.command.CommandRegistry;
import com.hosea.creditsimulator.controller.command.ExitCommand;
import com.hosea.creditsimulator.controller.command.ListSheetsCommand;
import com.hosea.creditsimulator.controller.command.LoadCommand;
import com.hosea.creditsimulator.controller.command.SaveSheetCommand;
import com.hosea.creditsimulator.controller.command.ShowCommand;
import com.hosea.creditsimulator.controller.command.SwitchSheetCommand;
import com.hosea.creditsimulator.factory.VehicleFactory;
import com.hosea.creditsimulator.repository.InMemorySheetRepository;
import com.hosea.creditsimulator.repository.SheetRepository;
import com.hosea.creditsimulator.service.InstallmentCalculator;
import com.hosea.creditsimulator.service.InterestRateCalculator;
import com.hosea.creditsimulator.service.LoanApplicationAssembler;
import com.hosea.creditsimulator.service.LoanSimulationService;
import com.hosea.creditsimulator.service.LoanValidator;
import com.hosea.creditsimulator.view.ConsoleInput;
import com.hosea.creditsimulator.view.ConsoleView;
import com.hosea.creditsimulator.view.CurrencyFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;

public final class CreditSimulatorApplication {

    private CreditSimulatorApplication() {
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Usage: credit_simulator [file_inputs.txt]");
            System.exit(2);
        }
        boolean fromFile = args.length == 1;
        try (BufferedReader reader = fromFile ? openFile(Path.of(args[0])) : openStdin()) {
            LoanDataClient loanDataClient = AppConfig.loanDataClient();
            buildController(reader, fromFile, System.out, loanDataClient, Clock.systemDefaultZone()).run();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Wires the application together. Kept separate from {@link #main} so tests can drive
     * the whole application with scripted input.
     *
     * @param echoInput echo every line read, used when input comes from a file
     */
    static CreditSimulatorController buildController(BufferedReader reader, boolean echoInput, PrintStream out,
                                                     LoanDataClient loanDataClient, Clock clock) {
        ConsoleView view = new ConsoleView(out, new CurrencyFormatter());
        InputPrompter prompter = new InputPrompter(new ConsoleInput(reader, echoInput ? out : null), view);

        LoanValidator validator = new LoanValidator(clock);
        VehicleFactory vehicleFactory = new VehicleFactory();
        LoanSimulationService simulationService =
                new LoanSimulationService(new InstallmentCalculator(new InterestRateCalculator()));
        LoanApplicationAssembler assembler = new LoanApplicationAssembler(validator, vehicleFactory);
        SimulationSession session = new SimulationSession();
        SheetRepository sheets = new InMemorySheetRepository();

        CommandRegistry registry = new CommandRegistry();
        registry.register(new CalculateCommand(prompter, validator, vehicleFactory, simulationService, session, view))
                .register(new LoadCommand(loanDataClient, assembler, simulationService, session, view))
                .register(new SaveSheetCommand(sheets, session, view))
                .register(new SwitchSheetCommand(sheets, session, view))
                .register(new ListSheetsCommand(sheets, session, view))
                .register(new ShowCommand(registry, view))
                .register(new ExitCommand(view));

        return new CreditSimulatorController(prompter, view, registry);
    }

    private static BufferedReader openFile(Path path) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IOException("File input tidak ditemukan: " + path);
        }
        return Files.newBufferedReader(path, StandardCharsets.UTF_8);
    }

    private static BufferedReader openStdin() {
        return new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }
}
