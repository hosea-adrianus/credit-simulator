package com.hosea.creditsimulator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hosea.creditsimulator.exception.LoanDataException;
import com.hosea.creditsimulator.model.LoanInput;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

public class HttpLoanDataClient implements LoanDataClient {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient;
    private final URI uri;
    private final ObjectMapper objectMapper;
    private final Duration timeout;

    public HttpLoanDataClient(URI uri) {
        this(HttpClient.newBuilder()
                        .connectTimeout(DEFAULT_TIMEOUT)
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build(),
                uri, new ObjectMapper(), DEFAULT_TIMEOUT);
    }

    public HttpLoanDataClient(HttpClient httpClient, URI uri, ObjectMapper objectMapper, Duration timeout) {
        this.httpClient = httpClient;
        this.uri = uri;
        this.objectMapper = objectMapper;
        this.timeout = timeout;
    }

    @Override
    public LoanInput fetch() {
        HttpResponse<String> response = send(HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("Accept", "application/json")
                .GET()
                .build());

        if (response.statusCode() / 100 != 2) {
            throw new LoanDataException("Web service mengembalikan HTTP " + response.statusCode() + " (" + uri + ")");
        }
        return parse(response.body()).toLoanInput();
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new LoanDataException("Web service tidak merespons dalam " + timeout.toSeconds() + " detik", e);
        } catch (IOException e) {
            throw new LoanDataException("Gagal menghubungi web service " + uri + ": " + e, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LoanDataException("Permintaan ke web service dibatalkan", e);
        }
    }

    private LoanDataResponse parse(String body) {
        try {
            LoanDataResponse data = objectMapper.readValue(body, LoanDataResponse.class);
            if (data == null) {
                throw new LoanDataException("Response web service kosong");
            }
            return data;
        } catch (JsonProcessingException e) {
            throw new LoanDataException("Response web service bukan JSON data pinjaman yang valid", e);
        }
    }
}
