package com.hosea.creditsimulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterestRateCalculatorTest {

    private final InterestRateCalculator calculator = new InterestRateCalculator();

    @ParameterizedTest(name = "car year {0} -> {1}%")
    @CsvSource({"1, 8", "2, 8.1", "3, 8.6", "4, 8.7", "5, 9.2", "6, 9.3"})
    void carRateFollowsYearlyIncrements(int year, String expected) {
        assertEquals(0, new BigDecimal(expected).compareTo(calculator.rateForYear(new BigDecimal("8"), year)));
    }

    @ParameterizedTest(name = "motorcycle year {0} -> {1}%")
    @CsvSource({"1, 9", "2, 9.1", "3, 9.6", "6, 10.3"})
    void motorcycleRateStartsFromNinePercent(int year, String expected) {
        assertEquals(0, new BigDecimal(expected).compareTo(calculator.rateForYear(new BigDecimal("9"), year)));
    }

    @Test
    void rejectsYearBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> calculator.rateForYear(BigDecimal.TEN, 0));
    }
}
