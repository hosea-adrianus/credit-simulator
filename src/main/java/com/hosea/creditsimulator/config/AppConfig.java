package com.hosea.creditsimulator.config;

import com.hosea.creditsimulator.client.HttpLoanDataClient;
import com.hosea.creditsimulator.client.LoanDataClient;

import java.net.URI;

public final class AppConfig {

    /** Mock web service that returns an existing loan calculation for the {@code load} command. */
    static final String LOAD_URL = "https://hosea.free.beeceptor.com/2cf4efab-33d9-4e4e-acd6-b8ca30566bb1";

    private AppConfig() {
    }

    public static LoanDataClient loanDataClient() {
        return new HttpLoanDataClient(URI.create(LOAD_URL));
    }
}
