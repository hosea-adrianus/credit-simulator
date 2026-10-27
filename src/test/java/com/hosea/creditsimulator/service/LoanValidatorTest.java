package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.exception.ValidationException;
import com.hosea.creditsimulator.model.VehicleCondition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoanValidatorTest {

    /** Fixes "today" to 2026 so the year rules are deterministic. */
    private final LoanValidator validator =
            new LoanValidator(Clock.fixed(Instant.parse("2026-06-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void newVehicleAcceptsCurrentAndPreviousYear() {
        assertEquals(2026, validator.validateYear("2026", VehicleCondition.NEW));
        assertEquals(2025, validator.validateYear(" 2025 ", VehicleCondition.NEW));
    }

    @Test
    void newVehicleRejectsOlderThanPreviousYear() {
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validateYear("2024", VehicleCondition.NEW));
        assertTrue(error.getMessage().contains("2025"));
    }

    @Test
    void usedVehicleAcceptsOlderYears() {
        assertEquals(2010, validator.validateYear("2010", VehicleCondition.USED));
    }

    @Test
    void rejectsFutureYear() {
        assertThrows(ValidationException.class, () -> validator.validateYear("2027", VehicleCondition.USED));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"25", "20250", "abcd", "20.5"})
    void rejectsYearThatIsNotFourDigits(String input) {
        assertThrows(ValidationException.class, () -> validator.validateYear(input, VehicleCondition.USED));
    }

    @Test
    void acceptsLoanAmountUpToOneBillion() {
        assertEquals(new BigDecimal("1000000000"), validator.validateLoanAmount("1000000000"));
        assertEquals(new BigDecimal("1"), validator.validateLoanAmount("1"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"0", "1000000001", "-5", "100.000.000", "1e9", "sepuluh", "9999999999999999"})
    void rejectsInvalidLoanAmount(String input) {
        assertThrows(ValidationException.class, () -> validator.validateLoanAmount(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "3", "6"})
    void acceptsTenureBetweenOneAndSix(String input) {
        assertEquals(Integer.parseInt(input), validator.validateTenure(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"0", "7", "-1", "satu", "1.5", "99999999999"})
    void rejectsInvalidTenure(String input) {
        assertThrows(ValidationException.class, () -> validator.validateTenure(input));
    }

    @Test
    void newVehicleNeedsAtLeastThirtyFivePercentDownPayment() {
        BigDecimal loan = new BigDecimal("100000000");

        assertEquals(new BigDecimal("35000000"), validator.validateDownPayment("35000000", VehicleCondition.NEW, loan));
        ValidationException error = assertThrows(ValidationException.class,
                () -> validator.validateDownPayment("34999999", VehicleCondition.NEW, loan));
        assertTrue(error.getMessage().contains("35%"));
    }

    @Test
    void usedVehicleNeedsAtLeastTwentyFivePercentDownPayment() {
        BigDecimal loan = new BigDecimal("100000000");

        assertEquals(new BigDecimal("25000000"), validator.validateDownPayment("25000000", VehicleCondition.USED, loan));
        assertThrows(ValidationException.class,
                () -> validator.validateDownPayment("24999999", VehicleCondition.USED, loan));
    }

    @Test
    void downPaymentMustBeLowerThanLoan() {
        BigDecimal loan = new BigDecimal("100000000");

        assertThrows(ValidationException.class,
                () -> validator.validateDownPayment("100000000", VehicleCondition.NEW, loan));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "-1"})
    void rejectsNonNumericDownPayment(String input) {
        assertThrows(ValidationException.class,
                () -> validator.validateDownPayment(input, VehicleCondition.NEW, new BigDecimal("100")));
    }
}
