package com.hosea.creditsimulator;

import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.model.LoanInput;
import com.hosea.creditsimulator.service.LoanDataClient;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives the fully wired application with scripted input, the same way a file input works.
 */
class CreditSimulatorApplicationTest {

    private static final Clock CLOCK_2026 = Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC);

    private static final LoanInput WEB_SERVICE_DATA =
            new LoanInput("Mobil", "Baru", "2025", "100000000", "3", "35000000");

    private static String run(String... lines) {
        return runWithClient(() -> WEB_SERVICE_DATA, lines);
    }

    private static String runWithClient(LoanDataClient client, String... lines) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(new StringReader(String.join("\n", lines)));

        CreditSimulatorApplication.buildController(reader, true, out, client, CLOCK_2026).run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void loadFetchesDataAndCalculatesAutomatically() {
        String output = run("load", "exit");

        assertTrue(output.contains("Kendaraan       : Mobil Baru (2025)"), output);
        assertTrue(output.contains("tahun 3 : Rp. 2,289,233.70/bln , Suku Bunga : 8,6%"), output);
    }

    @Test
    void loadReportsWebServiceFailureAndKeepsRunning() {
        String output = runWithClient(() -> {
            throw new LoanDataException("Web service mengembalikan HTTP 500");
        }, "load", "show", "exit");

        assertTrue(output.contains("[ERROR] Web service mengembalikan HTTP 500"), output);
        assertTrue(output.contains("Daftar command:"), output);
    }

    @Test
    void loadValidatesWebServiceData() {
        String output = runWithClient(() -> new LoanInput("Mobil", "Baru", "2025", "100000000", "3", "1000"),
                "load", "exit");

        assertTrue(output.contains("Data dari web service tidak valid: DP kendaraan baru minimal 35%"), output);
    }

    @Test
    void calculatesInstallmentsFromScriptedInput() {
        String output = run("calculate", "Mobil", "Baru", "2025", "100000000", "3", "35000000", "exit");

        assertTrue(output.contains("tahun 1 : Rp. 1,950,000.00/bln , Suku Bunga : 8%"), output);
        assertTrue(output.contains("tahun 2 : Rp. 2,107,950.00/bln , Suku Bunga : 8,1%"), output);
        assertTrue(output.contains("tahun 3 : Rp. 2,289,233.70/bln , Suku Bunga : 8,6%"), output);
        assertTrue(output.contains("Terima kasih"), output);
    }

    @Test
    void asksAgainUntilEachFieldIsValid() {
        String output = run("calculate",
                "truk", "motor",
                "lama", "bekas",
                "20", "2018",
                "2000000000", "50000000",
                "7", "1",
                "1000000", "12500000",
                "exit");

        assertTrue(output.contains("Jenis kendaraan harus Motor atau Mobil"), output);
        assertTrue(output.contains("Kondisi kendaraan harus Baru atau Bekas"), output);
        assertTrue(output.contains("Tahun kendaraan harus 4 digit angka"), output);
        assertTrue(output.contains("Jumlah pinjaman maksimal"), output);
        assertTrue(output.contains("Tenor harus 1-6 tahun"), output);
        assertTrue(output.contains("DP kendaraan bekas minimal 25%"), output);
        // (50,000,000 - 12,500,000) * 1.09 / 12
        assertTrue(output.contains("tahun 1 : Rp. 3,406,250.00/bln , Suku Bunga : 9%"), output);
    }

    @Test
    void rejectsOldYearForNewVehicle() {
        String output = run("calculate", "Mobil", "Baru", "2020", "exit");

        assertTrue(output.contains("Kendaraan baru minimal tahun 2025"), output);
        assertTrue(output.contains("perhitungan dibatalkan"), output);
    }

    @Test
    void showListsAllCommands() {
        String output = run("show", "exit");

        assertTrue(output.contains("Daftar command:"), output);
        assertTrue(output.contains("calculate"), output);
        assertTrue(output.contains("show"), output);
        assertTrue(output.contains("exit"), output);
    }

    @Test
    void commandsAreCaseInsensitive() {
        String output = run("SHOW", "Exit");

        assertTrue(output.contains("Daftar command:"), output);
        assertTrue(output.contains("Terima kasih"), output);
    }

    @Test
    void unknownCommandPointsToShow() {
        String output = run("hitung", "exit");

        assertTrue(output.contains("Command 'hitung' tidak dikenal. Ketik 'show'"), output);
    }

    @Test
    void stopsAtEndOfInputWithoutExit() {
        String output = run("show");

        assertTrue(output.contains("Daftar command:"), output);
        assertFalse(output.contains("Terima kasih"), output);
    }
}
