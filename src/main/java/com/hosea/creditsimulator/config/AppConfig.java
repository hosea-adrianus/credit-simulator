package com.hosea.creditsimulator.config;

import com.hosea.creditsimulator.client.HttpLoanDataClient;
import com.hosea.creditsimulator.client.LoanDataClient;
import com.hosea.creditsimulator.exception.LoanDataException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

public final class AppConfig {

    static final String LOAD_URL_ENV = "CREDIT_SIMULATOR_LOAD_URL";

    /** Mock web service used when {@value #LOAD_URL_ENV} is not set. */
    static final String DEFAULT_LOAD_URL =
            "https://hosea.free.beeceptor.com/2cf4efab-33d9-4e4e-acd6-b8ca30566bb1";

    private final Map<String, String> environment;
    private final String defaultLoadUrl;

    AppConfig(Map<String, String> environment, String defaultLoadUrl) {
        this.environment = environment;
        this.defaultLoadUrl = defaultLoadUrl;
    }

    public static AppConfig fromEnvironment() {
        return new AppConfig(System.getenv(), DEFAULT_LOAD_URL);
    }

    Optional<String> loadUrl() {
        String url = environment.getOrDefault(LOAD_URL_ENV, "").trim();
        if (url.isEmpty()) {
            url = defaultLoadUrl.trim();
        }
        return url.isEmpty() ? Optional.empty() : Optional.of(url);
    }

    /**
     * Uses the configured web service URL. Without a usable URL the {@code load} command
     * explains how to configure one instead of failing.
     */
    public LoanDataClient loanDataClient() {
        Optional<String> url = loadUrl();
        if (url.isEmpty()) {
            return () -> {
                throw new LoanDataException("URL web service belum diset. Set environment variable "
                        + LOAD_URL_ENV + ", contoh: export " + LOAD_URL_ENV + "=https://example.com/loan.json");
            };
        }
        try {
            URI uri = new URI(url.get());
            boolean httpScheme = "http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme());
            if (!httpScheme || uri.getHost() == null) {
                throw new URISyntaxException(url.get(), "harus berupa URL http:// atau https:// yang lengkap");
            }
            return new HttpLoanDataClient(uri);
        } catch (URISyntaxException e) {
            return () -> {
                throw new LoanDataException("URL web service tidak valid (" + LOAD_URL_ENV + "): " + e.getMessage());
            };
        }
    }
}
