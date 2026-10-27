package com.hosea.creditsimulator.controller.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public CommandRegistry register(Command command) {
        String key = command.name().toLowerCase(Locale.ROOT);
        if (commands.containsKey(key)) {
            throw new IllegalArgumentException("Command already registered: " + command.name());
        }
        commands.put(key, command);
        return this;
    }

    public Optional<Command> find(String name) {
        return Optional.ofNullable(commands.get(name.toLowerCase(Locale.ROOT)));
    }

    public List<Command> all() {
        return new ArrayList<>(commands.values());
    }
}
