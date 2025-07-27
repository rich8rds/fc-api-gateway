package com.favourite.collections.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommandLineRunnerConfig implements CommandLineRunner {

    private final OpenApiConfig openApiConfig;
    private RouteDefinitionLocator routeDefinitionLocator;

    @Override
    public void run(String... args) throws Exception {
        openApiConfig.apis(routeDefinitionLocator);
    }
}
