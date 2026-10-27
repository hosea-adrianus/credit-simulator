package com.hosea.creditsimulator.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hosea.creditsimulator.model.LoanInput;

/**
 * JSON body returned by the load web service, e.g.
 * <pre>{@code
 * {
 *   "vehicleType": "Mobil",
 *   "vehicleCondition": "Baru",
 *   "vehicleYear": 2025,
 *   "totalLoanAmount": 100000000,
 *   "loanTenure": 3,
 *   "downPayment": 35000000
 * }
 * }</pre>
 * Fields are read as text so numbers and strings are both accepted and validated
 * by the same rules as manual input.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
record LoanDataResponse(
        String vehicleType,
        String vehicleCondition,
        String vehicleYear,
        String totalLoanAmount,
        String loanTenure,
        String downPayment) {

    LoanInput toLoanInput() {
        return new LoanInput(vehicleType, vehicleCondition, vehicleYear, totalLoanAmount, loanTenure, downPayment);
    }
}
