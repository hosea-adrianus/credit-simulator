package com.hosea.creditsimulator.controller;

import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.view.ConsoleInput;
import com.hosea.creditsimulator.view.ConsoleView;

import java.util.Optional;
import java.util.function.Function;

/**
 * Asks for a single field and keeps asking until the answer is valid.
 */
public class InputPrompter {

    private final ConsoleInput input;
    private final ConsoleView view;

    public InputPrompter(ConsoleInput input, ConsoleView view) {
        this.input = input;
        this.view = view;
    }

    /**
     * @param label  prompt shown to the user
     * @param parser converts the raw answer, throwing {@link ValidationException} when it is invalid
     * @return the parsed value, or empty if the input ended before a valid answer was given
     */
    public <T> Optional<T> ask(String label, Function<String, T> parser) {
        while (true) {
            view.prompt(label);
            Optional<String> line = input.readLine();
            if (line.isEmpty()) {
                return Optional.empty();
            }
            try {
                return Optional.of(parser.apply(line.get()));
            } catch (ValidationException e) {
                view.printError(e.getMessage());
            }
        }
    }

    /**
     * Reads the next command line, or empty when the input has ended.
     */
    public Optional<String> readCommand() {
        view.promptCommand();
        return input.readLine();
    }
}
