/* Collections #2024 */
package com.favourite.collections.config;

import java.util.*;
import java.util.stream.Collectors;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

	 @Value("${favourite.openapi.dev-url}")
	 private String devUrl;

	 @Value("${favourite.openapi.prod-url}")
	 private String prodUrl;

	 @Bean
	 public OpenAPI myOpenAPI() {
	 Server devServer = new Server();
	 devServer.setUrl(devUrl);
	 devServer.setDescription("Server URL in Development environment");

	 Server prodServer = new Server();
	 prodServer.setUrl(prodUrl);
	 prodServer.setDescription("Server URL in Production environment");

	 Contact contact = new Contact();
	 contact.setEmail("favourdaniel74@gmail.com");
	 contact.setName("Favourite Collections");
	 contact.setUrl("https://www.favourite-collections.com");

	 License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

	 Info info = new Info().title("Favourite Collections API").version("1.0").contact(contact)
	 .description("This API exposes endpoints to manage tutorials.")
	 .termsOfService("https://www.favourite-collections.com/terms").license(mitLicense);

	 return new OpenAPI()
	 .security(List.of(new SecurityRequirement().addList("Bearer Authentication"),
	 new SecurityRequirement().addList("Basic Authentication")))
	 .components(new
			 Components().securitySchemes(createSecuritySchemesMap())).info(info)
	 .servers(List.of(devServer, prodServer));
	 }

	 private Map<String, SecurityScheme> createSecuritySchemesMap() {

	 SecurityScheme createAPIKeyScheme = new
	 SecurityScheme().type(SecurityScheme.Type.HTTP).bearerFormat("JWT")
	 .description("Enter a valid JWT.").scheme("bearer");

	 SecurityScheme createBasicScheme = new
	 SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
	 .description("Input username and password");

	 Map<String, SecurityScheme> securitySchemesMap = new HashMap<>();
	 securitySchemesMap.put("Basic Authentication", createBasicScheme);
	 securitySchemesMap.put("Bearer Authentication", createAPIKeyScheme);

	 return securitySchemesMap;
	 }


	@Bean
	public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
		log.info("Initializing OpenAPI groups for Swagger UI");

		List<RouteDefinition> definitions = Objects.requireNonNull(
				locator.getRouteDefinitions().collectList().block()
		);

		log.info("Found {} route definitions", definitions.size());

		List<GroupedOpenApi> groups = definitions.stream()
				.map(RouteDefinition::getId)
				.filter(id -> id.endsWith("-service"))
				.map(id -> id.replace("-service", "")) // extract group name
				.map(group -> GroupedOpenApi.builder()
						.group(group)
						.pathsToMatch("/" + group + "/**")
						.build())
				.collect(Collectors.toList());

		log.info("Total OpenAPI groups registered: {}", groups.size());
		return groups;
	}
}
