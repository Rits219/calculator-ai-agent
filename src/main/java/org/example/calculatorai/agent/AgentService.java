package org.example.calculatorai.agent;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public class AgentService {
    private static final Set<String> SUPPORTED_INTENTS =
            Set.of("ADD", "SUBTRACT", "MULTIPLY");

    private final IntentParser parser;
    private final CapabilityRegistry registry;

    public AgentService(
            IntentParser parser,
            CapabilityRegistry registry) {
        this.parser = parser;
        this.registry = registry;
    }

    public AgentResponse process(String message)
            throws IOException, InterruptedException {
        AgentResponse error = validateMessage(message);
        if (error != null) {
            return error;
        }

        JsonNode parsed = parser.parse(message);
        String intent = parsed.path("intent")
                .asText("").toUpperCase();
        AgentResponse intentError = validateIntent(intent, parsed);
        if (intentError != null) {
            return intentError;
        }

        JsonNode aNode = parsed.get("operandA");
        JsonNode bNode = parsed.get("operandB");
        AgentResponse operandError = validateOperand(aNode, bNode);
        if (operandError != null) {
            return operandError;
        }

        try {
            String aText = aNode.asText();
            String bText = bNode.asText();

            if (aText.length() > 100 || bText.length() > 100) {
                return error("CLARIFICATION",
                        "The numbers are too large.");
            }

            Integer a = Integer.valueOf(aText);
            Integer b = Integer.valueOf(bText);

            Integer result = registry.execute(intent, a, b);

            return new AgentResponse(
                    intent,
                    true,
                    result,
                    "Calculation completed successfully."
            );

        } catch (NumberFormatException e) {
            return error("CLARIFICATION",
                    "I couldn't interpret the numbers. Please try again.");
        }
    }

    private AgentResponse validateOperand(JsonNode aNode, JsonNode bNode) {
        if (aNode == null || aNode.isNull()
                || bNode == null || bNode.isNull()) {
            return error("CLARIFICATION",
                    "Please provide both numbers.");
        }
        return null;
    }

    private AgentResponse validateIntent(String intent, JsonNode parsed) {
        if (!registry.isRecognized(intent)) {
            return error("CLARIFICATION",
                    "I couldn't understand that request. "
                            + "Please rephrase it.");
        }

        if ("OUT_OF_SCOPE".equals(intent)) {
            return error(intent,
                    "I'm built only for calculations. "
                            + "I can help with addition and subtraction.");
        }

        if ("CLARIFICATION".equals(intent)) {
            String clarification = parsed.path("clarification")
                    .asText("");

            if (clarification.isBlank()) {
                clarification = "Please provide both numbers "
                        + "and specify the operation.";
            }

            return error(intent, clarification);
        }

        if (!SUPPORTED_INTENTS.contains(intent)) {
            return error(intent,
                    "That calculation isn't supported yet. "
                            + "Currently, I support addition, subtraction and multuplication.");
        }
        return null;
    }

    private AgentResponse validateMessage(String message) {
        if (message == null || message.isBlank()) {
            return error("CLARIFICATION",
                    "Please enter a calculation.");
        }

        if (message.length() > 2000) {
            return error("CLARIFICATION",
                    "Please keep your message under 2000 characters.");
        }
        return null;
    }

    private AgentResponse error(String intent, String message) {
        return new AgentResponse(intent, false, null, message);
    }
}
