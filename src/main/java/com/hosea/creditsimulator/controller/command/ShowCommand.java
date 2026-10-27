package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.view.ConsoleView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShowCommand implements Command {

    private final CommandRegistry registry;
    private final ConsoleView view;

    public ShowCommand(CommandRegistry registry, ConsoleView view) {
        this.registry = registry;
        this.view = view;
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return "Tampilkan semua command yang bisa digunakan";
    }

    @Override
    public CommandResult execute(List<String> args) {
        Map<String, String> usages = new LinkedHashMap<>();
        registry.all().forEach(command -> usages.put(command.usage(), command.description()));
        view.printCommands(usages);
        return CommandResult.CONTINUE;
    }
}
