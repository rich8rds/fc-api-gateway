package com.favourite.collections.feign;

import com.favourite.collections.commons.useradmin.data.RoleResponseData;
import com.favourite.collections.config.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", path = "/roles")
public interface UserClient {

    @GetMapping("/{roleId}")
    ResponseEntity<RoleResponseData> findRoleById(@PathVariable(name = "roleId") Long roleId,
                                                  @RequestParam(name = "isDisabled", defaultValue = "false") Boolean isDisabled);

    @GetMapping(
            value = "/name/{roleName}",
            consumes = ApiConstants.MEDIA_TYPE_JSON)
    ResponseEntity<RoleResponseData> findRoleByName(@PathVariable(name = "roleName") String roleName,
                                                    @RequestParam(name = "isDisabled", defaultValue = "false") Boolean isDisabled);

}
