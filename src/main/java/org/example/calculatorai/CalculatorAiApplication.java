package org.example.calculatorai;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.example.calculatorai.agent.AgentResource;

public class CalculatorAiApplication extends Application<CalculatorAiConfiguration> {

    public static void main(String[] args) throws Exception {
        new CalculatorAiApplication().run(args);
    }

    @Override
    public String getName() {
        return "calculator-ai-agent";
    }

    @Override
    public void initialize(
            Bootstrap<CalculatorAiConfiguration> bootstrap) {
        // Register bundles here when needed.
    }

    @Override
    public void run(
            CalculatorAiConfiguration configuration,
            Environment environment) {
        String model = System.getenv()
                .getOrDefault("OLLAMA_MODEL", "qwen2.5:3b");

        Injector injector = Guice.createInjector(new CalculatorAiModule(environment.getObjectMapper(), model));

        environment.jersey().register(injector.getInstance(AgentResource.class));
    }
}
