package com.feri.alessandro.attsw.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan("com.feri.alessandro.attsw.project.security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static final String LOGIN = "/login";

	@Bean
	public AuthenticationSuccessHandler getSuccessHandler() {
		return new UserAuthenticationSuccessHandler();
	}
	
	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(LOGIN).permitAll()
			.antMatchers("/registration").permitAll()
			.antMatchers("/", "/new", "/save", "/edit/**", "/search", "/delete", "/deleteAll").authenticated()
			.and().csrf().disable()
			.formLogin().successHandler(getSuccessHandler())
			.loginPage(LOGIN).failureUrl("/login?error=true")
			.usernameParameter("email")
			.passwordParameter("password")
			.and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl(LOGIN).and().exceptionHandling();
	}
}
