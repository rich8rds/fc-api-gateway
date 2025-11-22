package com.favourite.collections.feign;

import com.favourite.collections.commons.useradmin.data.RoleResponseData;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClientServiceImpl implements UserClient {

    private final WebClient webClient;

    public UserClientServiceImpl(@LoadBalanced WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("lb://user-service/roles")
                .build();
    }

    public Mono<RoleResponseData> findRoleById(Long roleId, Boolean isDisabled) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{roleId}")
                        .queryParam("isDisabled", isDisabled)
                        .build(roleId))
                .retrieve()
                .bodyToMono(RoleResponseData.class);
    }

    public Mono<RoleResponseData> findRoleByName(String roleName, Boolean isDisabled) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/name/{roleName}")
                        .queryParam("isDisabled", isDisabled)
                        .build(roleName))
                .retrieve()
                .bodyToMono(RoleResponseData.class);
    }

}
