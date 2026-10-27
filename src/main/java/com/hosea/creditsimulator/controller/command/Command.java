package com.hosea.creditsimulator.controller.command;

import java.util.List;

/**
 * A menu entry the user can type at the prompt.
 */
public interface Command {

    /**
     * Name typed by the user, e.g. {@code calculate}.
     */
    String name();

    /**
     * How to call it, including arguments, e.g. {@code save_sheet <nama>}.
     */
    default String usage() {
        return name();
    }

    String description();

    /**
     * @param args words typed after the command name
     */
    CommandResult execute(List<String> args);
}
