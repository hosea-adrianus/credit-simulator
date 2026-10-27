package com.hosea.creditsimulator.config;

import com.hosea.creditsimulator.client.HttpLoanDataClient;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class AppConfigTest {

    @Test
    void loadUrlIsAValidHttpsUrl() {
        URI uri = URI.create(AppConfig.LOAD_URL);

        assertEquals("https", uri.getScheme());
        assertEquals("hosea.free.beeceptor.com", uri.getHost());
    }

    @Test
    void createsHttpClientForLoad() {
        assertInstanceOf(HttpLoanDataClient.class, AppConfig.loanDataClient());
    }
}
