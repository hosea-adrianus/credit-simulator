package com.hosea.creditsimulator.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Optional;

/**
 * Reads input one line at a time from the keyboard or from an input file.
 *
 * <p>When reading from a file the line is echoed after the prompt, so the output
 * reads the same as an interactive session.
 */
public class ConsoleInput {

    private final BufferedReader reader;
    private final PrintStream echo;

    public ConsoleInput(BufferedReader reader, PrintStream echo) {
        this.reader = reader;
        this.echo = echo;
    }

    public Optional<String> readLine() {
        try {
            String line = reader.readLine();
            if (line != null && echo != null) {
                echo.println(line);
            }
            return Optional.ofNullable(line);
        } catch (IOException e) {
            throw new UncheckedIOException("Gagal membaca input", e);
        }
    }
}
