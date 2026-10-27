package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.controller.SimulationSession;
import com.hosea.creditsimulator.repository.SheetRepository;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.List;

/**
 * Lists the saved sheets and marks the active one.
 */
public class ListSheetsCommand implements Command {

    private final SheetRepository sheets;
    private final SimulationSession session;
    private final ConsoleView view;

    public ListSheetsCommand(SheetRepository sheets, SimulationSession session, ConsoleView view) {
        this.sheets = sheets;
        this.session = session;
        this.view = view;
    }

    @Override
    public String name() {
        return "list_sheets";
    }

    @Override
    public String description() {
        return "Tampilkan semua sheet yang sudah disimpan";
    }

    @Override
    public CommandResult execute(List<String> args) {
        List<String> names = sheets.names();
        if (names.isEmpty()) {
            view.printInfo("Belum ada sheet. Simpan hasil simulasi dengan 'save_sheet <nama>'.");
            return CommandResult.CONTINUE;
        }
        String active = session.activeSheet().orElse(null);
        view.printInfo("Daftar sheet:");
        names.forEach(name -> view.printInfo((name.equals(active) ? "* " : "  ") + name));
        return CommandResult.CONTINUE;
    }
}
