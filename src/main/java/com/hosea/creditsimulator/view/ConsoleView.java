package com.hosea.creditsimulator.view;

import com.hosea.creditsimulator.model.LoanApplication;
import com.hosea.creditsimulator.model.LoanSimulation;
import com.hosea.creditsimulator.model.YearlyInstallment;

import java.io.PrintStream;
import java.util.Map;

/**
 * Everything the user sees is written through this class.
 */
public class ConsoleView {

    private final PrintStream out;
    private final CurrencyFormatter formatter;

    public ConsoleView(PrintStream out, CurrencyFormatter formatter) {
        this.out = out;
        this.formatter = formatter;
    }

    public void printWelcome() {
        out.println("==============================================");
        out.println("  Credit Simulator - Simulasi Kredit Kendaraan");
        out.println("==============================================");
        out.println("Ketik 'show' untuk melihat daftar command.");
    }

    public void promptCommand() {
        out.println();
        out.print("> ");
        out.flush();
    }

    public void prompt(String label) {
        out.print(label + " : ");
        out.flush();
    }

    public void printInfo(String message) {
        out.println(message);
    }

    public void printError(String message) {
        out.println("[ERROR] " + message);
    }

    public void printCommands(Map<String, String> usageToDescription) {
        int width = usageToDescription.keySet().stream().mapToInt(String::length).max().orElse(0);
        out.println("Daftar command:");
        usageToDescription.forEach((usage, description) ->
                out.printf("  %-" + width + "s  %s%n", usage, description));
    }

    public void printSimulation(LoanSimulation simulation) {
        LoanApplication application = simulation.application();
        out.println("---------------- Hasil Simulasi ----------------");
        out.println("Kendaraan       : " + application.vehicle());
        out.println("Jumlah Pinjaman : " + formatter.formatRupiah(application.loanAmount()));
        out.println("Jumlah DP       : " + formatter.formatRupiah(application.downPayment()));
        out.println("Pokok Pinjaman  : " + formatter.formatRupiah(application.principal()));
        out.println("Tenor           : " + application.tenureYears() + " tahun");
        out.println();
        for (YearlyInstallment installment : simulation.installments()) {
            out.println("tahun " + installment.year() + " : "
                    + formatter.formatRupiah(installment.monthlyInstallment()) + "/bln , Suku Bunga : "
                    + formatter.formatRate(installment.interestRate()));
        }
        out.println("------------------------------------------------");
    }
}
