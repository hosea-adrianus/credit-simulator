package com.hosea.creditsimulator.config;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void loadUrlIsAValidHttpsUrl() {
        URI uri = URI.create(AppConfig.LOAD_URL);

        assertEquals("https", uri.getScheme());
        assertNotNull(uri.getHost());
    }
}
