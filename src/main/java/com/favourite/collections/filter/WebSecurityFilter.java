/* Collections #2025 */
package com.favourite.collections.filter;

import com.favourite.collections.commons.core.config.JwtConfig;
import com.favourite.collections.commons.core.exceptions.AbstractPlatformException;
import com.favourite.collections.service.impl.AppUserDetailsService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;


import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebSecurityFilter implements WebFilter {

	private final RouteValidator routeValidator;
	private final JwtConfig jwtConfig;
	private final AppUserDetailsService appUserDetailsService;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		boolean isPermitted = routeValidator.getPathVariables(request);
		//og.info("request: {}", request.getURI());
		//log.info("isPermitted: {}", isPermitted);


		if (BooleanUtils.isFalse(isPermitted)) {
			String token = extractToken(request);
			String username = jwtConfig.extractUsername(token);

			return appUserDetailsService.findByUsername(username)
					.map(userDetails -> {
						UsernamePasswordAuthenticationToken authentication =
								new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        return (SecurityContext) new SecurityContextImpl(authentication);
					})
					.flatMap(securityContext ->
							chain.filter(exchange)
									.contextWrite(ReactiveSecurityContextHolder
											.withSecurityContext(Mono.just(securityContext)))
					);
		}

		return chain.filter(exchange);
	}

	private String extractToken(ServerHttpRequest request) {
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		throw new AbstractPlatformException("error.jwt.validation.check", "Invalid Token", 401);
	}
}
