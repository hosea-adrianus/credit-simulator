package com.hosea.creditsimulator.model;

import java.math.BigDecimal;

/**
 * Monthly installment that applies during one year of the loan.
 *
 * @param year               1-based year of the tenure
 * @param monthlyInstallment amount paid each month in that year
 * @param interestRate       interest rate in percent used for that year
 */
public record YearlyInstallment(int year, BigDecimal monthlyInstallment, BigDecimal interestRate) {
}
