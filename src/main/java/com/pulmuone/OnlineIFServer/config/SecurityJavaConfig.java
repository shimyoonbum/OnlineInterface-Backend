package com.pulmuone.OnlineIFServer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pulmuone.OnlineIFServer.filter.CorsOriginFilter;
import com.pulmuone.OnlineIFServer.filter.JwtAuthenticationFilter;
import com.pulmuone.OnlineIFServer.filter.JwtAuthorizationFilter;
import com.pulmuone.OnlineIFServer.repository.UserRepository;
import com.pulmuone.OnlineIFServer.util.JwtUtil;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private CorsOriginFilter corsConfig;
	
	@Autowired
	private UserRepository userRepository;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.cors().disable().csrf().disable()
				// 폼 로그인 창 나오지 않도록 설정
				.formLogin().disable().headers().frameOptions().disable().and()
				.addFilter(corsConfig.corsFilter())
				.addFilter(new JwtAuthenticationFilter(authenticationManager()))
				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))
				// 세션 관련
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}