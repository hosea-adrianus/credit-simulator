package com.hosea.creditsimulator;

import java.util.Map;
import java.util.Optional;

final class AppConfig {

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

    static AppConfig fromEnvironment() {
        return new AppConfig(System.getenv(), DEFAULT_LOAD_URL);
    }

    Optional<String> loadUrl() {
        String url = environment.getOrDefault(LOAD_URL_ENV, "").trim();
        if (url.isEmpty()) {
            url = defaultLoadUrl.trim();
        }
        return url.isEmpty() ? Optional.empty() : Optional.of(url);
    }
}
