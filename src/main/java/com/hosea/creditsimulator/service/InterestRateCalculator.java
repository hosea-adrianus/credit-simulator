package com.hosea.creditsimulator.service;

import java.math.BigDecimal;

/**
 * Computes the interest rate for each year of a loan.
 *
 * <p>The first year uses the vehicle's base rate. Every following year adds 0.1% (even years)
 * or 0.5% (odd years), e.g. 8% → 8.1% → 8.6% → 8.7% → 9.2% → 9.3%. Following the sample
 * (8% → 8.1% → 8.6%), the 2-yearly increase replaces the yearly one in odd years.
 */
public class InterestRateCalculator {

    private static final BigDecimal YEARLY_INCREASE = new BigDecimal("0.1");
    private static final BigDecimal TWO_YEARLY_INCREASE = new BigDecimal("0.5");

    public BigDecimal rateForYear(BigDecimal baseRate, int year) {
        if (year < 1) {
            throw new IllegalArgumentException("year must be >= 1, was " + year);
        }
        BigDecimal rate = baseRate;
        for (int current = 2; current <= year; current++) {
            rate = rate.add(isEven(current) ? YEARLY_INCREASE : TWO_YEARLY_INCREASE);
        }
        return rate;
    }

    private static boolean isEven(int year) {
        return year % 2 == 0;
    }
}
