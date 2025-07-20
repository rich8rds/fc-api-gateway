/* Collections #2024 */
package com.favourite.collections.service;


import com.favourite.collections.commons.core.data.CommandResult;
import com.favourite.collections.commons.useradmin.data.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AuthService {

	Mono<ResponseEntity<CommandResult>> loginUserIn(LoginData loginData);

	ResponseEntity<CommandResult> register(RegistrationData registerData);

	ResponseEntity<CommandResult> verifyUserVerificationToken(String token);

	ResponseEntity<CommandResult> resendVerificationToken(String token);

	ResponseEntity<CommandResult> updatePassword(UpdatePasswordData updatePasswordData);

	ResponseEntity<CommandResult> getForgotPasswordToken(ForgotPasswordData forgotPasswordData);

	ResponseEntity<CommandResult> changePasswordWithToken(String token, ChangePasswordData changePasswordData);

	Mono<RoleResponseData>  test(String roleName);
}
