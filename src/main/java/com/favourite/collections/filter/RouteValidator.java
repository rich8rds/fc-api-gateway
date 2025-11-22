/* Collections #2025 */
package com.favourite.collections.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import static com.favourite.collections.util.RequestMethods.ALL;

@Component
public class RouteValidator {

	public final Map<Pattern, List<String>> openApiEndpoints = new HashMap<>();

	public RouteValidator() {
		openApiEndpoints.put(compilePattern("^/swagger-ui.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("^/v3/api-docs.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/swagger-ui(/.*|$)"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("index"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("^/css"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("^/js"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("^/js"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/actuator/info"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/actuator/.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/api/v1/auth(/.*|$)"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/api/v1/notifications/.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/api/v1/user-service/states/product-service"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/v2/api-docs"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/v3/api-docs/.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/notifications-service/v3/api-docs"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/user-service/v3/api-docs"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/user-service/.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/product-service/v3/api-docs"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/dashboard-service/v3/api-docs"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/configuration"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/webjars/.*"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/swagger-ui.html"), List.of(ALL.name()));
		openApiEndpoints.put(compilePattern("/eureka"), List.of(ALL.name()));
	}

    public boolean getPathVariables(ServerHttpRequest request) {
        boolean pathVariables = false;
		try {
			String path = request.getURI().getPath();
            String requestMethod = String.valueOf(request.getMethod());
			//log.info("request.getPath(): {}", request.getPath());
            //log.info("request.getURI(): {}", request.getURI());

			for (Pattern registeredPattern : openApiEndpoints.keySet()) {
                final List<String> requestMethods = openApiEndpoints.get(registeredPattern);
				boolean pathsMatch = registeredPattern.matcher(path).matches();
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

    private Pattern compilePattern(String pattern) {
        return Pattern.compile(pattern);
    }
}
