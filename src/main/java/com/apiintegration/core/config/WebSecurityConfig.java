package com.apiintegration.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.apiintegration.core.jwt.JwtAuthenticationEntryPoint;
import com.apiintegration.core.jwt.JwtRequestFilter;
import com.apiintegration.core.jwt.LogEnhancerFilter;
import com.apiintegration.core.utils.UserRole;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private UserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// configure AuthenticationManager so that it knows from where to load
		// user for matching credentials
		// Use BCryptPasswordEncoder
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf().disable()
				// Don't need to authenticate this particular requests
				.authorizeRequests().antMatchers("/user/**").permitAll()

				// Authenticate Control for Role 'USER'.
				.antMatchers("/account/create", "/account/join/**").hasRole(UserRole.USER)

				.antMatchers("/account/delete", "/account/confirm-delete/{token}").hasRole(UserRole.OWNER)

				// Authenticate Control for Role 'LEAD'.
				.antMatchers("/account/update", "/account/invite-member", "/account/update-member",
						"/account/remove-member/**", "/account/list-member", "/account/list-project",
						"/project/delete/**")
				.hasAnyRole(UserRole.LEAD, UserRole.OWNER)

				// Authenticate Control for Role 'SUPERDEV'.
				.antMatchers("/project/create", "/project/update")
				.hasAnyRole(UserRole.SUPERDEV, UserRole.LEAD, UserRole.OWNER)

				// Authenticate Control for Role 'DEV'
				.antMatchers("/project/get/{id}", "/project/list", "/services/**", "/api/**")
				.hasAnyRole(UserRole.DEV, UserRole.SUPERDEV, UserRole.LEAD, UserRole.OWNER)

				// Handle exceptions for unauthenticated entries
				.and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.cors();

		// Add a filter to validate the tokens with every request
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		httpSecurity.addFilterAfter(new LogEnhancerFilter(), JwtRequestFilter.class);
	}

	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();

		config.setAllowCredentials(true);
		config.addAllowedOrigin("*"); // this allows all origin
		config.addAllowedHeader("*"); // this allows all headers
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}