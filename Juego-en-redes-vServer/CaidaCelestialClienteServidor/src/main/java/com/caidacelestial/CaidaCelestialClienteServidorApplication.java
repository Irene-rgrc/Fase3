package com.caidacelestial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SpringBootApplication
public class CaidaCelestialClienteServidorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaidaCelestialClienteServidorApplication.class, args);
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	return (web) -> web.ignoring().requestMatchers(
	new AntPathRequestMatcher("/**")
	);
	}
}
