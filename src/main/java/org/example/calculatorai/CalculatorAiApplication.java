package org.example.calculatorai;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.example.calculatorai.agent.AgentResource;

import java.util.EnumSet;

public class CalculatorAiApplication extends Application<CalculatorAiConfiguration> {

    private void configureCors(Environment environment) {
        FilterRegistration.Dynamic cors =
                environment.servlets()
                        .addFilter("CORS", CrossOriginFilter.class);

        cors.setInitParameter(
                "allowedOrigins",
                "http://127.0.0.1:5500,http://localhost:5500,https://calculator-ui-yv8r.onrender.com"
        );

        cors.setInitParameter(
                "allowedMethods",
                "GET,POST,PUT,DELETE,OPTIONS,HEAD"
        );

        cors.setInitParameter(
                "allowedHeaders",
                "Content-Type,Accept,Origin,X-Requested-With"
        );

        cors.addMappingForUrlPatterns(
                EnumSet.allOf(DispatcherType.class),
                true,
                "/*"
        );
    }

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
        configureCors(environment);
        String model = System.getenv()
                .getOrDefault("OLLAMA_MODEL", "qwen2.5:3b");

        Injector injector = Guice.createInjector(new CalculatorAiModule(environment.getObjectMapper(), model));

        environment.jersey().register(injector.getInstance(AgentResource.class));
    }
}
