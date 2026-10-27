package com.hosea.creditsimulator.view;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Formats money and interest rates the way the problem statement shows them:
 * {@code Rp. 3,500,000.00} and {@code 8,1%}.
 */
public class CurrencyFormatter {

    private static final DecimalFormatSymbols US_SYMBOLS = DecimalFormatSymbols.getInstance(Locale.US);

    public String formatRupiah(BigDecimal amount) {
        DecimalFormat format = new DecimalFormat("#,##0.00", US_SYMBOLS);
        format.setRoundingMode(RoundingMode.HALF_UP);
        return "Rp. " + format.format(amount);
    }

    public String formatRate(BigDecimal ratePercent) {
        BigDecimal normalized = ratePercent.stripTrailingZeros();
        if (normalized.scale() < 0) {
            normalized = normalized.setScale(0);
        }
        return normalized.toPlainString().replace('.', ',') + "%";
    }
}
