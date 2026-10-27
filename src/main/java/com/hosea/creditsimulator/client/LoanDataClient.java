package com.hosea.creditsimulator.client;

import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.model.LoanInput;

@FunctionalInterface
public interface LoanDataClient {

    /**
     * @return the raw loan fields, not validated yet
     * @throws LoanDataException when the data cannot be retrieved
     */
    LoanInput fetch();
}
