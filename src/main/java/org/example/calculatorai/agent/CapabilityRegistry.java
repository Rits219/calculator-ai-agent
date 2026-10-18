package org.example.calculatorai.agent;

import com.google.inject.Inject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

public class CapabilityRegistry {

    private final CalculatorClient calculatorClient;

    private final Set<String> recognizedIntents = Set.of(
            "ADD",
            "SUBTRACT",
            "MULTIPLY",
            "DIVIDE",
            "OTHER_CALCULATION",
            "OUT_OF_SCOPE",
            "CLARIFICATION"
    );

    @Inject
    public CapabilityRegistry(CalculatorClient calculatorClient) {
        this.calculatorClient = calculatorClient;
    }

    public boolean isRecognized(String intent) {
        return recognizedIntents.contains(intent);
    }

    public boolean isSupported(String intent) {
        return "ADD".equals(intent)
                || "SUBTRACT".equals(intent);
    }

    public Integer execute(
            String intent,
            Integer a,
            Integer b) throws IOException, InterruptedException {

        return switch (intent) {
            case "ADD" -> calculatorClient.add(a, b);
            case "SUBTRACT" -> calculatorClient.subtract(a, b);
            case "MULTIPLY" -> calculatorClient.multiply(a, b);
            default -> throw new IllegalArgumentException(
                    "Unsupported operation: " + intent);
        };
    }
}
