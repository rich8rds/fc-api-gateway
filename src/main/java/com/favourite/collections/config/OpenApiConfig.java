/* Collections #2024 */
package com.favourite.collections.config;

import java.util.List;
import java.util.Objects;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

	// @Value("${favourite.openapi.dev-url}")
	// private String devUrl;
	//
	// @Value("${favourite.openapi.prod-url}")
	// private String prodUrl;
	//
	// @Bean
	// public OpenAPI myOpenAPI() {
	// Server devServer = new Server();
	// devServer.setUrl(devUrl);
	// devServer.setDescription("Server URL in Development environment");
	//
	// Server prodServer = new Server();
	// prodServer.setUrl(prodUrl);
	// prodServer.setDescription("Server URL in Production environment");
	//
	// Contact contact = new Contact();
	// contact.setEmail("favourdaniel74@gmail.com");
	// contact.setName("Favourite Collections");
	// contact.setUrl("https://www.favourite-collections.com");
	//
	// License mitLicense = new License().name("MIT
	// License").url("https://choosealicense.com/licenses/mit/");
	//
	// Info info = new Info().title("Favourite Collections
	// API").version("1.0").contact(contact)
	// .description("This API exposes endpoints to manage tutorials.")
	// .termsOfService("https://www.favourite-collections.com/terms").license(mitLicense);
	//
	// return new OpenAPI()
	// .security(List.of(new SecurityRequirement().addList("Bearer Authentication"),
	// new SecurityRequirement().addList("Basic Authentication")))
	// .components(new
	// Components().securitySchemes(createSecuritySchemesMap())).info(info)
	// .servers(List.of(devServer, prodServer));
	// }
	//
	// private Map<String, SecurityScheme> createSecuritySchemesMap() {
	//
	// SecurityScheme createAPIKeyScheme = new
	// SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT")
	// .description("Enter a valid JWT.").scheme("bearer");
	//
	// SecurityScheme createBasicScheme = new
	// SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
	// .description("Input username and password");
	//
	// Map<String, SecurityScheme> securitySchemesMap = new HashMap<>();
	// securitySchemesMap.put("Basic Authentication", createBasicScheme);
	// securitySchemesMap.put("Bearer Authentication", createAPIKeyScheme);
	//
	// return securitySchemesMap;
	// }

	@Bean
	public CommandLineRunner openApiGroups(RouteDefinitionLocator locator,
			SwaggerUiConfigParameters swaggerUiParameters) {
		return args -> {
			List<RouteDefinition> routeDefinitions = Objects
					.requireNonNull(locator.getRouteDefinitions().collectList().block());

			//log.info("RouteDefinitions: {}", routeDefinitions);
			//log.info("RouteDefinitions Size: {}", routeDefinitions.size());

			routeDefinitions.stream().map(RouteDefinition::getId).filter(id -> id.matches(".*-service"))
					.map(id -> id.replace("-service", "")).forEach(swaggerUiParameters::addGroup);
		};
	}
}
