package com.hosea.creditsimulator;

import java.util.Map;
import java.util.Optional;

/**
 * Runtime settings read from environment variables.
 */
final class AppConfig {

    static final String LOAD_URL_ENV = "CREDIT_SIMULATOR_LOAD_URL";

    /** Used when {@value #LOAD_URL_ENV} is not set. Empty means no default web service. */
    static final String DEFAULT_LOAD_URL = "";

    private final Map<String, String> environment;
    private final String defaultLoadUrl;

    AppConfig(Map<String, String> environment, String defaultLoadUrl) {
        this.environment = environment;
        this.defaultLoadUrl = defaultLoadUrl;
    }

    static AppConfig fromEnvironment() {
        return new AppConfig(System.getenv(), DEFAULT_LOAD_URL);
    }

    /**
     * URL of the JSON web service used by the {@code load} command, if one is configured.
     */
    Optional<String> loadUrl() {
        String url = environment.getOrDefault(LOAD_URL_ENV, "").trim();
        if (url.isEmpty()) {
            url = defaultLoadUrl.trim();
        }
        return url.isEmpty() ? Optional.empty() : Optional.of(url);
    }
}
