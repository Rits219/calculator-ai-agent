package org.example.calculatorai;

import com.google.inject.AbstractModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.example.calculatorai.agent.AgentService;
import org.example.calculatorai.agent.CalculatorClient;
import org.example.calculatorai.agent.CapabilityRegistry;
import org.example.calculatorai.agent.IntentParser;

public class CalculatorAiModule extends AbstractModule {
    private final ObjectMapper objectMapper;
    private final String model;

    public CalculatorAiModule(ObjectMapper objectMapper, String model) {
        this.objectMapper = objectMapper;
        this.model = model;
    }

    @Provides
    @Singleton
    public IntentParser provideIntentParser() {
        return new IntentParser(objectMapper, model);
    }

    @Provides
    @Singleton
    public CapabilityRegistry provideCapabilityRegistry(CalculatorClient calculatorClient) {
        return new CapabilityRegistry(calculatorClient);
    }

    @Provides
    @Singleton
    public AgentService provideAgentService(
            IntentParser intentParser,
            CapabilityRegistry capabilityRegistry) {
        return new AgentService(intentParser, capabilityRegistry);
    }

    @Provides
    @Singleton
    public CalculatorClient provideCalculatorClient() {
        return new CalculatorClient(objectMapper);
    }
}
