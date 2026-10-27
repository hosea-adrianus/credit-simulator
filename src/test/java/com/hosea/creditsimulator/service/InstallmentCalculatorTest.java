package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.model.YearlyInstallment;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstallmentCalculatorTest {

    private final InstallmentCalculator calculator = new InstallmentCalculator(new InterestRateCalculator());

    @Test
    void calculatesCarLoanOverThreeYears() {
        // Loan 100,000,000 - DP 35,000,000 = principal 65,000,000
        List<YearlyInstallment> result = calculator.calculate(new BigDecimal("65000000"), new BigDecimal("8"), 3);

        assertEquals(3, result.size());
        assertInstallment(result.get(0), 1, "1950000.00", "8");
        assertInstallment(result.get(1), 2, "2107950.00", "8.1");
        assertInstallment(result.get(2), 3, "2289233.70", "8.6");
    }

    @Test
    void calculatesSingleYearCarLoan() {
        List<YearlyInstallment> result = calculator.calculate(new BigDecimal("12000000"), new BigDecimal("8"), 1);

        assertEquals(1, result.size());
        assertInstallment(result.get(0), 1, "1080000.00", "8");
    }

    @Test
    void calculatesSingleYearMotorcycleLoan() {
        List<YearlyInstallment> result = calculator.calculate(new BigDecimal("12000000"), new BigDecimal("9"), 1);

        assertInstallment(result.get(0), 1, "1090000.00", "9");
    }

    @Test
    void sixYearLoanHasIncreasingInstallments() {
        List<YearlyInstallment> result = calculator.calculate(new BigDecimal("650000000"), new BigDecimal("8"), 6);

        assertEquals(6, result.size());
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i).monthlyInstallment().compareTo(result.get(i - 1).monthlyInstallment()) > 0,
                    "installment of year " + (i + 1) + " should be higher than the previous year");
        }
        assertEquals(0, new BigDecimal("9.3").compareTo(result.get(5).interestRate()));
    }

    @Test
    void installmentsAreRoundedToTwoDecimals() {
        List<YearlyInstallment> result = calculator.calculate(new BigDecimal("10000001"), new BigDecimal("9"), 6);

        result.forEach(installment -> assertEquals(2, installment.monthlyInstallment().scale()));
    }

    @Test
    void rejectsTenureBelowOne() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(BigDecimal.TEN, BigDecimal.ONE, 0));
    }

    private static void assertInstallment(YearlyInstallment actual, int year, String monthly, String rate) {
        assertEquals(year, actual.year());
        assertEquals(new BigDecimal(monthly), actual.monthlyInstallment());
        assertEquals(0, new BigDecimal(rate).compareTo(actual.interestRate()));
    }
}
