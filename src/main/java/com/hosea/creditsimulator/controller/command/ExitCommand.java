package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.view.ConsoleView;

import java.util.List;

public class ExitCommand implements Command {

    private final ConsoleView view;

    public ExitCommand(ConsoleView view) {
        this.view = view;
    }

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String description() {
        return "Keluar dari aplikasi";
    }

    @Override
    public CommandResult execute(List<String> args) {
        view.printInfo("Terima kasih telah menggunakan Credit Simulator.");
        return CommandResult.EXIT;
    }
}
