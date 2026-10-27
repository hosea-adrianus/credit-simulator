package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.repository.SheetRepository;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.List;

public class SwitchSheetCommand implements Command {

    private final SheetRepository sheets;
    private final SimulationSession session;
    private final ConsoleView view;

    public SwitchSheetCommand(SheetRepository sheets, SimulationSession session, ConsoleView view) {
        this.sheets = sheets;
        this.session = session;
        this.view = view;
    }

    @Override
    public String name() {
        return "switch_sheet";
    }

    @Override
    public String usage() {
        return "switch_sheet <nama>";
    }

    @Override
    public String description() {
        return "Pindah ke sheet <nama> dan tampilkan hasilnya";
    }

    @Override
    public CommandResult execute(List<String> args) {
        String name = SheetNames.fromArgs(args, usage());
        LoanSimulation simulation = sheets.find(name).orElseThrow(() -> new ValidationException(
                "Sheet '" + name + "' tidak ditemukan. Ketik 'list_sheets' untuk melihat sheet yang ada."));

        session.activateSheet(name, simulation);
        view.printInfo("Sheet aktif: " + name);
        view.printSimulation(simulation);
        return CommandResult.CONTINUE;
    }
}
