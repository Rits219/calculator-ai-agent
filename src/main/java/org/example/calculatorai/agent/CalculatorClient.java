package org.example.calculatorai.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public class CalculatorClient {
    private static final String BASE_URL =
            "https://calculator-backend-g8oq.onrender.com";

    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    public CalculatorClient(ObjectMapper mapper) {
        this.mapper = mapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public Integer add(Integer a, Integer b)
            throws IOException, InterruptedException {
        return execute("/calculator/add", a, b);
    }

    public Integer subtract(Integer a, Integer b)
            throws IOException, InterruptedException {
        return execute("/calculator/subtract", a, b);
    }

    public Integer multiply(Integer a, Integer b)
            throws IOException, InterruptedException {
        return execute("/calculator/multiply", a, b);
    }

    public Integer devide(Integer a, Integer b)
            throws IOException, InterruptedException {
        return execute("/calculator/devide", a, b);
    }

    private Integer execute(
            String path,
            Integer a,
            Integer b)
            throws IOException, InterruptedException {

        ObjectNode payload = mapper.createObjectNode();
        payload.put("a", a);
        payload.put("b", b);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            throw new IOException(
                    "Calculator API returned HTTP "
                            + response.statusCode());
        }

        JsonNode body = mapper.readTree(response.body());
        JsonNode resultNode = body;

        if (resultNode == null
                || (!resultNode.isNumber()
                && !resultNode.isTextual())) {
            throw new IOException(
                    "Unexpected calculator API response format");
        }

        try {
            return Integer.valueOf(resultNode.asText());
        } catch (NumberFormatException e) {
            throw new IOException(
                    "Calculator API returned an invalid result", e);
        }
    }
}
