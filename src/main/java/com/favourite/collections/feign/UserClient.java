package com.favourite.collections.feign;

import com.favourite.collections.commons.useradmin.data.RoleResponseData;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface UserClient {

    Mono<RoleResponseData> findRoleById(@PathVariable(name = "roleId") Long roleId,
                                          @RequestParam(name = "isDisabled", defaultValue = "false") Boolean isDisabled);

    Mono<RoleResponseData> findRoleByName(@PathVariable(name = "roleName") String roleName,
                                                    @RequestParam(name = "isDisabled", defaultValue = "false") Boolean isDisabled);

}
