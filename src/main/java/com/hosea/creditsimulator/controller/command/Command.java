package com.hosea.creditsimulator.controller.command;

import java.util.List;

public interface Command {

    String name();

    /**
     * How to call it, including arguments, e.g. {@code save_sheet <nama>}.
     */
    default String usage() {
        return name();
    }

    String description();

    CommandResult execute(List<String> args);
}
