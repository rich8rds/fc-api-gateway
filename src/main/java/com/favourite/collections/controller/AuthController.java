package com.favourite.collections.controller;

import com.favourite.collections.commons.core.data.CommandResult;
import com.favourite.collections.commons.useradmin.data.ChangePasswordData;
import com.favourite.collections.commons.useradmin.data.ForgotPasswordData;
import com.favourite.collections.commons.useradmin.data.LoginData;
import com.favourite.collections.commons.useradmin.data.RegistrationData;
import com.favourite.collections.commons.useradmin.data.RoleResponseData;
import com.favourite.collections.commons.useradmin.data.UpdatePasswordData;
import com.favourite.collections.commons.useradmin.exception.ConstraintValidationException;
import com.favourite.collections.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.favourite.collections.config.ApiConstants.AUTH_BASE_URL;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_BASE_URL)
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public Mono<ResponseEntity<CommandResult>> login(@RequestBody LoginData loginData) {
		return authService.loginUserIn(loginData);
	}

	@PostMapping("/register")
	public ResponseEntity<CommandResult> register(@RequestBody @Valid RegistrationData registerData) {
		if (!registerData.passwordsMatch()) {
			throw new ConstraintValidationException("error.auth.passwords.do.not.match", "Passwords do not match");
		}
		return authService.register(registerData);
	}

	@GetMapping("/verify-registration")
	public ResponseEntity<CommandResult> verifyAccount(@RequestParam(name = "token") String token) {
		return authService.verifyUserVerificationToken(token);
	}

	@GetMapping("/resend-verification-token")
	public ResponseEntity<CommandResult> resendVerificationToken(@RequestParam String token) {
		return authService.resendVerificationToken(token);
	}

	@PostMapping("/update-password")
	public ResponseEntity<CommandResult> updatePassword(@RequestBody UpdatePasswordData updatePasswordData) {
		if (!updatePasswordData.passwordsMatch()) {
			throw new ConstraintValidationException("error.auth.passwords.do.not.match", "Passwords do not match");
		}
		return authService.updatePassword(updatePasswordData);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<CommandResult> forgotPassword(@RequestBody ForgotPasswordData forgotPasswordData) {
		return authService.getForgotPasswordToken(forgotPasswordData);
	}

	@PostMapping("/change-password")
	public ResponseEntity<CommandResult> resetPassword(@RequestParam String token,
			@Valid @RequestBody ChangePasswordData changePasswordData) {
		if (!changePasswordData.passwordsMatch()) {
			throw new ConstraintValidationException("error.auth.passwords.do.not.match", "Passwords do not match");
		}
		return authService.changePasswordWithToken(token, changePasswordData);
	}

}
