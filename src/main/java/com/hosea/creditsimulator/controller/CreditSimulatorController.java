package com.hosea.creditsimulator.controller;

import com.hosea.creditsimulator.controller.command.Command;
import com.hosea.creditsimulator.controller.command.CommandRegistry;
import com.hosea.creditsimulator.controller.command.CommandResult;
import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreditSimulatorController {

    private final InputPrompter prompter;
    private final ConsoleView view;
    private final CommandRegistry registry;

    public CreditSimulatorController(InputPrompter prompter, ConsoleView view, CommandRegistry registry) {
        this.prompter = prompter;
        this.view = view;
        this.registry = registry;
    }

    public void run() {
        view.printWelcome();
        while (true) {
            Optional<String> line = prompter.readCommand();
            if (line.isEmpty()) {
                view.printInfo("");
                return;
            }
            String trimmed = line.get().trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (dispatch(trimmed) == CommandResult.EXIT) {
                return;
            }
        }
    }

    private CommandResult dispatch(String commandLine) {
        List<String> words = Arrays.asList(commandLine.split("\\s+"));
        String name = words.get(0);
        List<String> args = words.subList(1, words.size());

        Optional<Command> command = registry.find(name);
        if (command.isEmpty()) {
            view.printError("Command '" + name + "' tidak dikenal. Ketik 'show' untuk melihat daftar command.");
            return CommandResult.CONTINUE;
        }
        try {
            return command.get().execute(args);
        } catch (ValidationException e) {
            view.printError(e.getMessage());
        } catch (RuntimeException e) {
            view.printError("Terjadi kesalahan tak terduga: " + e.getMessage());
        }
        return CommandResult.CONTINUE;
    }
}
