package com.user.auth.security;

import com.user.auth.multitenancy.TenantInterceptor;
import com.user.auth.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author makarand
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtAuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private JwtProvider jwtProvider;

	@Autowired UserDetailsService userDetailsService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TenantInterceptor tenantInterceptor;
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()

				.antMatchers("/swagger-ui.html", "/user/login","/user/forgotPassword","/user/resetPassword"
				,"/user/changePassword");

		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.GET, "/", "/swagger-resources/**", "/webjars/**", "/v2/api-docs/**",
						"/configuration/**", "/images/**", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css",
						"/**/*.js")
				.permitAll().anyRequest().authenticated().and().exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(tenantInterceptor, UsernamePasswordAuthenticationFilter.class);
	}

}
