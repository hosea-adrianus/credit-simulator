package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.repository.SheetRepository;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.List;

/**
 * Saves the simulation currently on screen as a named sheet.
 */
public class SaveSheetCommand implements Command {

    private final SheetRepository sheets;
    private final SimulationSession session;
    private final ConsoleView view;

    public SaveSheetCommand(SheetRepository sheets, SimulationSession session, ConsoleView view) {
        this.sheets = sheets;
        this.session = session;
        this.view = view;
    }

    @Override
    public String name() {
        return "save_sheet";
    }

    @Override
    public String usage() {
        return "save_sheet <nama>";
    }

    @Override
    public String description() {
        return "Simpan hasil simulasi yang sedang tampil ke sheet bernama <nama>";
    }

    @Override
    public CommandResult execute(List<String> args) {
        String name = SheetNames.fromArgs(args, usage());
        LoanSimulation simulation = session.activeSimulation().orElseThrow(() -> new ValidationException(
                "Belum ada simulasi untuk disimpan. Jalankan 'calculate' atau 'load' dulu."));

        boolean replaced = sheets.save(name, simulation);
        session.activateSheet(name, simulation);
        view.printInfo(replaced ? "Sheet '" + name + "' diperbarui." : "Sheet '" + name + "' disimpan.");
        return CommandResult.CONTINUE;
    }
}
