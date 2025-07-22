/* Collections #2024 */
package com.favourite.collections.service.impl;

import com.favourite.collections.commons.core.config.JwtConfig;
import com.favourite.collections.commons.core.data.CommandResult;
import com.favourite.collections.commons.core.data.CommandResultBuilder;
import com.favourite.collections.commons.core.exceptions.AbstractPlatformException;
import com.favourite.collections.commons.useradmin.data.ChangePasswordData;
import com.favourite.collections.commons.useradmin.data.ForgotPasswordData;
import com.favourite.collections.commons.useradmin.data.LoginData;
import com.favourite.collections.commons.useradmin.data.RegistrationData;
import com.favourite.collections.commons.useradmin.data.RoleResponseData;
import com.favourite.collections.commons.useradmin.data.UpdatePasswordData;
import com.favourite.collections.commons.useradmin.domain.AppUser;
import com.favourite.collections.commons.useradmin.domain.Token;
import com.favourite.collections.commons.useradmin.exception.ConstraintValidationException;
import com.favourite.collections.commons.useradmin.repository.AppUserRepository;
import com.favourite.collections.commons.useradmin.repository.TokenRepository;
import com.favourite.collections.commons.useradmin.util.AppContextUser;
import com.favourite.collections.commons.useradmin.util.TokenGenerator;
import com.favourite.collections.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AppUserDetailsService userDetailsService;
	private final ReactiveAuthenticationManager authenticationManager;
	private final JwtConfig jwtConfig;
	private final AppUserRepository appUserRepository;
	private final TokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AppContextUser appContextUser;
	private final TokenGenerator tokenGenerator;

	//private final UserClient userClient;


	@Override
	public Mono<ResponseEntity<CommandResult>> loginUserIn(LoginData loginData) {

		try {
			UserDetails user = userDetailsService.findByUsername(loginData.getEmail()).block();
			if(user == null) {
				throw new UsernameNotFoundException(loginData.getEmail());
			}
			if (!user.isEnabled()) {
				throw new UsernameNotFoundException(
						"error.user.not.verified.or.active: Check your email to be verified!");
			}
			if (!user.isAccountNonLocked()) {
				throw new AbstractPlatformException("error.message.invalid.account",
						"Please contact the administrator");
			}

			return authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword())).map(
							authentication -> {
							String token = this.jwtConfig.generateToken(authentication);
							//log.info("Generated token for login: {}", token);
							return ResponseEntity.ok(new CommandResultBuilder().response("Login Successful")
									.token(token).build());
					}
			).onErrorResume(err -> {
				//log.error(err.getMessage());
				return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new CommandResultBuilder().response("Invalid credentials")
						.build()));
			});

		} catch (BadCredentialsException e) {
			//log.error("LogInUserError: {}", e.getMessage(), e);
			throw new AbstractPlatformException("error.msg.auth.login", "Incorrect username or password", 401);
		} catch (UsernameNotFoundException e) {
			//log.info("User not found: {}", e.getMessage());
			throw new AbstractPlatformException("error.msg.auth.login", "Incorrect username or password", 404);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<CommandResult> register(RegistrationData registerData) {

		String email = registerData.getEmail();
		String firstname = registerData.getFirstname();
		String lastname = registerData.getLastname();
		String phoneNumber = registerData.getPhoneNumber();
		String password = registerData.getPassword();
		String roleName = registerData.getRoleName();

		boolean existsByEmail = this.appUserRepository.existsByEmail(email);
		if (BooleanUtils.isTrue(existsByEmail)) {
			throw new AbstractPlatformException("error.msg.user.email.already.exists",
					"User with email " + email + " already exists", 409);
		}

//		ResponseEntity<RoleResponseData> roleResponseData = this.userClient.findRoleByName(roleName, false);
//
//		if(roleResponseData.hasBody()) {
//			RoleResponseData role = roleResponseData.getBody();
//			if(role == null) {
//				roleResponseData = this.userClient.findRoleByName("CUSTOMER", false);
//				if(roleResponseData.hasBody()) {
//					 role = roleResponseData.getBody();
//				} else {
//					throw new AbstractPlatformException("error.infrastructure.role.not.found", "Role not found!");
//				}
//			}
//		}

		// todo: Create and add cart
		AppUser newAppuser = AppUser.builder().email(email).firstname(firstname).lastname(lastname).phoneNo(phoneNumber)
				.password(passwordEncoder.encode(password))
				.role(null).build();

		newAppuser = this.appUserRepository.save(newAppuser);

		//log.info("AppUserId: {}", newAppuser.getId());
		Token token = tokenGenerator.generateToken(5L, ChronoUnit.MINUTES, newAppuser.getId());
		this.tokenRepository.save(token);

		// todo: sendEmail To user
		//log.info("Token: {}", token);
		return ResponseEntity.ok(new CommandResultBuilder().entityId(newAppuser.getId())
				.response("Registration Successful").message("Check your email to get verified").build());
	}

	@Override
	public ResponseEntity<CommandResult> verifyUserVerificationToken(String token) {
		Token verificationToken = getToken(token);
		Long id = verificationToken.getAppUserId();
		AppUser appUser = appUserRepository.findById(id == null ? 0L : id)
				.orElseThrow(() -> new UsernameNotFoundException("User with email does not exist"));

		appUser.setVerified(true);
		appUser.setActive(true);
		appUserRepository.save(appUser);

		tokenRepository.delete(verificationToken);

		return ResponseEntity
				.ok(new CommandResultBuilder().entityId(appUser.getId()).response("Account verification successful")
						.message("Login to your account to start shopping!").build());
	}

	private Token getToken(String token) {
		Token verificationToken = this.tokenRepository.findByToken(token)
				.orElseThrow(() -> new AbstractPlatformException("error.msg.auth.token.not.found", "Token Not Found"));

		long expirationTime = verificationToken.getExpirationTime();
		long now = Instant.now().getEpochSecond();

		if (now > expirationTime) {
			throw new ConstraintValidationException("error.msg.auth.token.expired",
					"Token has expired. Try resending verification token to email");
		}
		return verificationToken;
	}

	@Override
	public ResponseEntity<CommandResult> resendVerificationToken(String token) {
		Token verificationToken = tokenRepository.findByToken(token)
				.orElseThrow(() -> new AbstractPlatformException("error.auth.msg.token.not.found", "Token Not Found"));

		Long id = verificationToken.getAppUserId();

		if (verificationToken.getExpirationTime() > Instant.now().getEpochSecond()) {
			throw new BadCredentialsException("Token has expired. Resend token again");
		}

		AppUser appUser = appUserRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User with email does not exist"));

		tokenRepository.delete(verificationToken);
		// todo: send email

		return ResponseEntity.ok(new CommandResultBuilder().entityId(appUser.getId())
				.response("Token sent successfully").message("Check your email to get verified").build());
	}

	@Override
	public ResponseEntity<CommandResult> updatePassword(UpdatePasswordData updatePasswordData) {
		String email = this.appContextUser.authenticated().getEmail();

		String oldPassword = updatePasswordData.getOldPassword();
		String newPassword = updatePasswordData.getNewPassword();
		String confirmNewPassword = updatePasswordData.getConfirmNewPassword();

		AppUser appUser = appUserRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Does Not Exist"));

		String dbPassword = appUser.getPassword();

		if (!passwordEncoder.matches(oldPassword, dbPassword)) {
			throw new ConstraintValidationException("error.msg.auth.passwords.do.not.match", "Passwords do not match!");
		}

		if (!confirmNewPassword.equals(newPassword)) {
			updatePasswordData.setConfirmNewPassword(newPassword);
		}

		appUser.setPassword(passwordEncoder.encode(newPassword));
		appUserRepository.save(appUser);

		return ResponseEntity.ok(
				new CommandResultBuilder().entityId(appUser.getId()).response("Password Successfully Changed").build());
	}

	@Override
	public ResponseEntity<CommandResult> getForgotPasswordToken(ForgotPasswordData forgotPasswordData) {
		AppUser appUser = appUserRepository.findByEmail(forgotPasswordData.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User Does Not Exist"));

		// completeEvent(appUser, FORGOTPASSWORD, request);
		// todo: sent to email
		return ResponseEntity.ok(new CommandResultBuilder().entityId(appUser.getId())
				.response("Check your email to change password.").build());
	}

	@Override
	public ResponseEntity<CommandResult> changePasswordWithToken(String token, ChangePasswordData changePasswordData) {
		Token verificationToken = getToken(token);

		AppUser appUser = appUserRepository.findById(verificationToken.getAppUserId())
				.orElseThrow(() -> new UsernameNotFoundException("User with email does not exist"));

		String newPassword = changePasswordData.getNewPassword();
		appUser.setPassword(passwordEncoder.encode(newPassword));
		appUserRepository.save(appUser);
		tokenRepository.delete(verificationToken);

		return ResponseEntity.ok(new CommandResultBuilder().entityId(appUser.getId())
				.response("Password Successfully Changed!").build());
	}

	@Override
	public Mono<RoleResponseData>  test(String roleName) {

//		Mono<RoleResponseData> roleResponseDataMono = Mono.fromCallable(() -> userClient.findRoleById(1L, false))
//				.subscribeOn(Schedulers.boundedElastic()) // offload to safe thread
//				.map(ResponseEntity::getBody);

//		Mono<RoleResponseData> roleResponseDataMono = Mono.fromCallable(() -> userClient.findRoleByName(roleName, false))
//				.subscribeOn(Schedulers.boundedElastic()) // offload to safe thread
//				.map(ResponseEntity::getBody);
//
//		//ResponseEntity<RoleResponseData> roleResponseData = this.userClient.findRoleByName(roleName, false);
//
//		roleResponseDataMono.map(role -> {
//			String roleNameString = role.getName();
//			// ... do something with roleName
//			//log.info("roleName: {}", roleNameString);
//			if(roleNameString == null) {
//				Mono<RoleResponseData> roleResponseDataMono2 = Mono.fromCallable(() -> userClient.findRoleByName("CUSTOMER", false))
//						.subscribeOn(Schedulers.boundedElastic()) // offload to safe thread
//						.map(ResponseEntity::getBody);
//
//				roleResponseDataMono.map(role2 -> {
//					String roleNameString2 = role.getName();
//					// ... do something with roleName
//					if(roleNameString2 == null) {
//						throw new AbstractPlatformException("error.infrastructure.role.not.found", "Role not found!");
//					}
//					//log.info("role2: {}", role2);
//					return role2;
//				});
//			}
//			//log.info("role: {}", role);
//			return role;
//		}).onErrorResume(Mono::error);


		//return roleResponseDataMono;
		return Mono.just(new RoleResponseData());
	}
}
