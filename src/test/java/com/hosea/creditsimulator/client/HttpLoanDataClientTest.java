package com.hosea.creditsimulator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.model.LoanInput;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs against a real HTTP server on localhost (the JDK's built-in HttpServer), so no
 * network access or mocking library is needed.
 */
class HttpLoanDataClientTest {

    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void mapsJsonWithNumericFields() {
        respond("/loan", 200, """
                {
                  "vehicleType": "Mobil",
                  "vehicleCondition": "Baru",
                  "vehicleYear": 2025,
                  "totalLoanAmount": 100000000,
                  "loanTenure": 3,
                  "downPayment": 35000000,
                  "unknownField": "ignored"
                }
                """);

        LoanInput input = client("/loan").fetch();

        assertEquals(new LoanInput("Mobil", "Baru", "2025", "100000000", "3", "35000000"), input);
    }

    @Test
    void mapsJsonWithTextFields() {
        respond("/loan", 200, """
                {"vehicleType":"motor","vehicleCondition":"bekas","vehicleYear":"2019",
                 "totalLoanAmount":"50000000","loanTenure":"2","downPayment":"12500000"}
                """);

        assertEquals(new LoanInput("motor", "bekas", "2019", "50000000", "2", "12500000"), client("/loan").fetch());
    }

    @Test
    void missingFieldsAreLeftForValidation() {
        respond("/loan", 200, "{\"vehicleType\":\"Mobil\"}");

        LoanInput input = client("/loan").fetch();

        assertEquals("Mobil", input.vehicleType());
        assertEquals(null, input.downPayment());
    }

    @Test
    void failsOnHttpErrorStatus() {
        respond("/loan", 404, "not found");

        LoanDataException error = assertThrows(LoanDataException.class, () -> client("/loan").fetch());
        assertTrue(error.getMessage().contains("HTTP 404"), error.getMessage());
    }

    @Test
    void failsOnInvalidJson() {
        respond("/loan", 200, "<html>oops</html>");

        assertThrows(LoanDataException.class, () -> client("/loan").fetch());
    }

    @Test
    void failsOnEmptyJsonBody() {
        respond("/loan", 200, "null");

        assertThrows(LoanDataException.class, () -> client("/loan").fetch());
    }

    @Test
    void failsWhenServerIsUnreachable() {
        int port = server.getAddress().getPort();
        server.stop(0);

        HttpLoanDataClient client = new HttpLoanDataClient(URI.create("http://127.0.0.1:" + port + "/loan"));

        assertThrows(LoanDataException.class, client::fetch);
    }

    @Test
    void failsOnTimeout() {
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            exchange.close();
        });
        HttpLoanDataClient client = new HttpLoanDataClient(HttpClient.newHttpClient(), uri("/slow"),
                new ObjectMapper(), Duration.ofMillis(200));

        LoanDataException error = assertThrows(LoanDataException.class, client::fetch);
        assertTrue(error.getMessage().contains("tidak merespons"), error.getMessage());
    }

    private void respond(String path, int status, String body) {
        server.createContext(path, exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
    }

    private HttpLoanDataClient client(String path) {
        return new HttpLoanDataClient(uri(path));
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path);
    }
}
