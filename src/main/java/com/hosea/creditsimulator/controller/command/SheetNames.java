package com.hosea.creditsimulator.controller.command;

import com.hosea.creditsimulator.exception.ValidationException;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Reads and validates the sheet name argument shared by the sheet commands.
 */
final class SheetNames {

    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_-]{1,30}");

    private SheetNames() {
    }

    static String fromArgs(List<String> args, String usage) {
        if (args.size() != 1) {
            throw new ValidationException("Format: " + usage);
        }
        String name = args.get(0);
        if (!VALID_NAME.matcher(name).matches()) {
            throw new ValidationException("Nama sheet hanya boleh huruf, angka, '_' atau '-' (maks 30 karakter)");
        }
        return name;
    }
}
