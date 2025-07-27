/* Collections #2025 */
package com.favourite.collections.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import static com.favourite.collections.util.RequestMethods.ALL;

@Component
public class RouteValidator {

	public final Map<String, List<String>> openApiEndpoints = new HashMap<>();

	public RouteValidator() {
		openApiEndpoints.put("/", List.of(ALL.name()));
		openApiEndpoints.put("/favicon.ico", List.of(ALL.name()));
		openApiEndpoints.put("index", List.of(ALL.name()));
		openApiEndpoints.put("/css", List.of(ALL.name()));
		openApiEndpoints.put("/js", List.of(ALL.name()));
		openApiEndpoints.put("/actuator/info", List.of(ALL.name()));
		openApiEndpoints.put("/actuator/**", List.of(ALL.name()));
		openApiEndpoints.put("/api/v1/auth/**", List.of(ALL.name()));
		openApiEndpoints.put("/api/v1/notifications", List.of(ALL.name()));
		openApiEndpoints.put("/api/v1/notifications/**", List.of(ALL.name()));
		openApiEndpoints.put("/api/v1/user-service/states/product-service", List.of(ALL.name()));
		openApiEndpoints.put("/v2/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/v3/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/v3/api-docs/swagger-config", List.of(ALL.name()));
		openApiEndpoints.put("/notifications-service/v3/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/user-service/v3/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/user-service/**", List.of(ALL.name()));
		openApiEndpoints.put("/product-service/v3/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/dashboard-service/v3/api-docs", List.of(ALL.name()));
		openApiEndpoints.put("/configuration", List.of(ALL.name()));
		openApiEndpoints.put("/swagger/**", List.of(ALL.name()));
		openApiEndpoints.put("/swagger-ui/**", List.of(ALL.name()));
		openApiEndpoints.put("/webjars/**", List.of(ALL.name()));
		openApiEndpoints.put("/swagger-ui.html", List.of(ALL.name()));
		openApiEndpoints.put("/eureka", List.of(ALL.name()));
	}

    public boolean getPathVariables(ServerHttpRequest request) {
        boolean pathVariables = false;
		try {
			final AntPathMatcher pathMatcher = new AntPathMatcher();
			String path = request.getURI().getPath();
            String requestMethod = String.valueOf(request.getMethod());
			//log.info("request.getPath(): {}", request.getPath());
            //log.info("request.getURI(): {}", request.getURI());

			for (String registeredPattern : openApiEndpoints.keySet()) {
                final List<String> requestMethods = openApiEndpoints.get(registeredPattern);
				boolean pathsMatch = pathMatcher.match(registeredPattern, path);
				boolean requestMethodsMatch = (requestMethods.contains(requestMethod) || requestMethods.contains(ALL.name()));
				if(BooleanUtils.isTrue(pathsMatch && requestMethodsMatch)) {
					return true;
				}
			}
			return pathVariables;
		} catch (Exception we) {
			//log.warn("getPathVariables: ", we);
		}
        return pathVariables;
	}
}
