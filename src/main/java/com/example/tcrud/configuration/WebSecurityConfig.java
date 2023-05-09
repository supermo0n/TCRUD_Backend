package com.example.tcrud.configuration;

import com.example.tcrud.security.jwt.AuthEntryPointJwt;
import com.example.tcrud.security.jwt.AuthTokenFilter;
import com.example.tcrud.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true)
public class WebSecurityConfig {

  @Autowired
UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


//  스프링 시큐리티 룰 무시 URL - JS/IMAGES/CSS
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().antMatchers("/js/**", "/images/**", "/css/**");
  }

//  스프링 시큐리티 룰 무시 URL
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.cors()
            .and().
            csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            // 세션X, JWT 사용
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//            TODO: /api/** 통과(접속허용) 수정
            .authorizeRequests().antMatchers("/", "/**","/api/**").permitAll()
            // 그 외 URL 접근은 인증을 필요로 처리
            .anyRequest().authenticated();

    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 토큰 필터 적용
    
    return http.build();
  }
}








