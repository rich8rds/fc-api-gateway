/* Request Relay MidLinkService */
package com.favourite.collections.config;

import com.favourite.collections.commons.core.data.ApiError;
import org.springframework.core.io.buffer.DataBuffer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AuthEntryPoint implements ServerAuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		ApiError errorResponse = ApiError.builder()
				.status(HttpStatus.UNAUTHORIZED)
				.debugMessage(ex.getMessage())
				.message("You're not authorized to access this resource!")
				.path(exchange.getRequest().getPath().value())
				.build();

		return writeErrorResponse(exchange, errorResponse, HttpStatus.UNAUTHORIZED);
	}

	private Mono<Void> writeErrorResponse(ServerWebExchange exchange, ApiError errorResponse, HttpStatus status) {
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

			exchange.getResponse().setStatusCode(status);
			exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

			return exchange.getResponse().writeWith(Mono.just(buffer));
		} catch (Exception e) {
			return Mono.error(e);
		}
	}
}
