package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.model.YearlyInstallment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates the monthly installment for every year of the tenure.
 *
 * <p>Each year the remaining balance is charged that year's interest and spread evenly over
 * the months that are left. What is paid during the year is then subtracted to get the
 * balance for the next year.
 */
public class InstallmentCalculator {

    private static final int MONTHS_PER_YEAR = 12;
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int MONEY_SCALE = 2;

    private final InterestRateCalculator interestRateCalculator;

    public InstallmentCalculator(InterestRateCalculator interestRateCalculator) {
        this.interestRateCalculator = interestRateCalculator;
    }

    public List<YearlyInstallment> calculate(BigDecimal principal, BigDecimal baseRate, int tenureYears) {
        if (tenureYears < 1) {
            throw new IllegalArgumentException("tenureYears must be >= 1, was " + tenureYears);
        }
        List<YearlyInstallment> installments = new ArrayList<>(tenureYears);
        BigDecimal remaining = principal;

        for (int year = 1; year <= tenureYears; year++) {
            BigDecimal rate = interestRateCalculator.rateForYear(baseRate, year);
            BigDecimal balanceWithInterest = remaining.multiply(BigDecimal.ONE.add(rate.divide(HUNDRED)));
            int monthsLeft = (tenureYears - year + 1) * MONTHS_PER_YEAR;

            BigDecimal monthly = balanceWithInterest.divide(BigDecimal.valueOf(monthsLeft), MONEY_SCALE, RoundingMode.HALF_UP);
            installments.add(new YearlyInstallment(year, monthly, rate));

            remaining = balanceWithInterest.subtract(monthly.multiply(BigDecimal.valueOf(MONTHS_PER_YEAR)));
        }
        return installments;
    }
}
