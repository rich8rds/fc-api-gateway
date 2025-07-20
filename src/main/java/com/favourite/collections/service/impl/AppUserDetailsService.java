/* Collections #2024 */
package com.favourite.collections.service.impl;

import com.favourite.collections.commons.useradmin.domain.AppUser;
import com.favourite.collections.commons.useradmin.repository.AppUserRepository;
import com.favourite.collections.commons.core.config.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements ReactiveUserDetailsService {
	private final AppUserRepository personRepository;

	@Override
	public Mono<UserDetails> findByUsername(String email) {
		AppUser dbUser = personRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Not Found"));
		return Mono.just(new AppUserDetails(dbUser));
	}
}
