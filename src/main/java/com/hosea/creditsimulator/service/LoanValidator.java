package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.VehicleCondition;
import com.hosea.creditsimulator.model.VehicleType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Year;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses and validates each loan field. Every method throws {@link ValidationException}
 * with a user-facing message when the value breaks a rule.
 */
public class LoanValidator {

    public static final BigDecimal MAX_LOAN_AMOUNT = new BigDecimal("1000000000");
    public static final int MIN_TENURE_YEARS = 1;
    public static final int MAX_TENURE_YEARS = 6;

    private static final Pattern FOUR_DIGITS = Pattern.compile("\\d{4}");
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    private static final int MAX_AMOUNT_DIGITS = 15;

    private final Clock clock;

    public LoanValidator(Clock clock) {
        this.clock = clock;
    }

    public VehicleType parseVehicleType(String raw) {
        return VehicleType.fromLabel(raw);
    }

    public VehicleCondition parseVehicleCondition(String raw) {
        return VehicleCondition.fromLabel(raw);
    }

    public int validateYear(String raw, VehicleCondition condition) {
        String value = required(raw, "Tahun kendaraan");
        if (!FOUR_DIGITS.matcher(value).matches()) {
            throw new ValidationException("Tahun kendaraan harus 4 digit angka (input: '" + value + "')");
        }
        int year = Integer.parseInt(value);
        int currentYear = Year.now(clock).getValue();
        if (year > currentYear) {
            throw new ValidationException("Tahun kendaraan tidak boleh lebih dari " + currentYear);
        }
        if (condition == VehicleCondition.NEW && year < currentYear - 1) {
            throw new ValidationException("Kendaraan baru minimal tahun " + (currentYear - 1));
        }
        return year;
    }

    public BigDecimal validateLoanAmount(String raw) {
        BigDecimal amount = parseAmount(raw, "Jumlah pinjaman");
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah pinjaman harus lebih dari 0");
        }
        if (amount.compareTo(MAX_LOAN_AMOUNT) > 0) {
            throw new ValidationException("Jumlah pinjaman maksimal Rp. " + format(MAX_LOAN_AMOUNT) + " (1 miliar)");
        }
        return amount;
    }

    public int validateTenure(String raw) {
        String value = required(raw, "Tenor");
        if (!DIGITS.matcher(value).matches() || value.length() > 2) {
            throw new ValidationException("Tenor harus berupa angka " + MIN_TENURE_YEARS + "-" + MAX_TENURE_YEARS
                    + " tahun (input: '" + value + "')");
        }
        int tenure = Integer.parseInt(value);
        if (tenure < MIN_TENURE_YEARS || tenure > MAX_TENURE_YEARS) {
            throw new ValidationException("Tenor harus " + MIN_TENURE_YEARS + "-" + MAX_TENURE_YEARS + " tahun");
        }
        return tenure;
    }

    public BigDecimal validateDownPayment(String raw, VehicleCondition condition, BigDecimal loanAmount) {
        BigDecimal downPayment = parseAmount(raw, "Jumlah DP");
        BigDecimal minimum = loanAmount.multiply(condition.minDownPaymentRatio()).setScale(2, RoundingMode.HALF_UP);
        if (downPayment.compareTo(minimum) < 0) {
            int percent = condition.minDownPaymentRatio().movePointRight(2).intValue();
            throw new ValidationException("DP kendaraan " + condition.label().toLowerCase(Locale.ROOT)
                    + " minimal " + percent + "% dari jumlah pinjaman (Rp. " + format(minimum) + ")");
        }
        if (downPayment.compareTo(loanAmount) >= 0) {
            throw new ValidationException("Jumlah DP harus lebih kecil dari jumlah pinjaman");
        }
        return downPayment;
    }

    private BigDecimal parseAmount(String raw, String fieldName) {
        String value = required(raw, fieldName);
        if (!DIGITS.matcher(value).matches()) {
            throw new ValidationException(fieldName + " harus berupa angka tanpa titik/koma (input: '" + value + "')");
        }
        if (value.length() > MAX_AMOUNT_DIGITS) {
            throw new ValidationException(fieldName + " terlalu besar");
        }
        return new BigDecimal(value);
    }

    private static String required(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new ValidationException(fieldName + " wajib diisi");
        }
        return raw.trim();
    }

    private static String format(BigDecimal amount) {
        return String.format(Locale.US, "%,.2f", amount);
    }
}
