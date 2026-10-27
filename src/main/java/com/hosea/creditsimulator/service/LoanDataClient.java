package com.hosea.creditsimulator.service;

import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.model.LoanInput;

/**
 * Source of an existing loan calculation, used by the {@code load} command.
 */
@FunctionalInterface
public interface LoanDataClient {

    /**
     * @return the raw loan fields, not validated yet
     * @throws LoanDataException when the data cannot be retrieved
     */
    LoanInput fetch();
}
