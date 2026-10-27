package com.hosea.creditsimulator;

import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.service.HttpLoanDataClient;
import com.hosea.creditsimulator.service.LoanDataClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    private static final String DEFAULT_URL = "https://default.example.com/loan.json";

    @Test
    void environmentVariableOverridesDefault() {
        AppConfig config = new AppConfig(Map.of(AppConfig.LOAD_URL_ENV, " https://example.com/loan.json "), DEFAULT_URL);

        assertEquals(Optional.of("https://example.com/loan.json"), config.loadUrl());
        assertInstanceOf(HttpLoanDataClient.class, CreditSimulatorApplication.createLoanDataClient(config));
    }

    @Test
    void blankEnvironmentVariableFallsBackToDefault() {
        AppConfig config = new AppConfig(Map.of(AppConfig.LOAD_URL_ENV, "  "), DEFAULT_URL);

        assertEquals(Optional.of(DEFAULT_URL), config.loadUrl());
    }

    @Test
    void missingUrlExplainsHowToConfigureIt() {
        AppConfig config = new AppConfig(Map.of(), "");
        LoanDataClient client = CreditSimulatorApplication.createLoanDataClient(config);

        assertEquals(Optional.empty(), config.loadUrl());
        LoanDataException error = assertThrows(LoanDataException.class, client::fetch);
        assertTrue(error.getMessage().contains(AppConfig.LOAD_URL_ENV), error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ftp://example.com/loan.json", "not a url", "http:no-host"})
    void invalidUrlIsReportedWhenLoading(String url) {
        LoanDataClient client = CreditSimulatorApplication.createLoanDataClient(
                new AppConfig(Map.of(AppConfig.LOAD_URL_ENV, url), ""));

        LoanDataException error = assertThrows(LoanDataException.class, client::fetch);
        assertTrue(error.getMessage().contains("tidak valid"), error.getMessage());
    }
}
