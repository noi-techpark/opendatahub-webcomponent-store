// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.SupplierJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Bearer-only API: JWTs issued by the configured Keycloak realm are validated
 * as an OAuth2 resource server, no session is kept.
 */
@Configuration
public class SecurityConfig {

	/**
	 * The issuer is resolved lazily, so the application can start while Keycloak is unreachable.
	 */
	@Bean
	JwtDecoder jwtDecoder(@Value("${keycloak.auth-server-url}") String authServerUrl,
						  @Value("${keycloak.realm}") String realm) {
		var issuer = authServerUrl.replaceAll("/+$", "") + "/realms/" + realm;
		return new SupplierJwtDecoder(() -> JwtDecoders.fromIssuerLocation(issuer));
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			.csrf(AbstractHttpConfigurer::disable)
			.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS).permitAll()
				.requestMatchers("/admin/**").authenticated()
				.anyRequest().permitAll())
			.oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()))
			.build();
	}
}
