package com.skkil.sync.config;

import com.skkil.sync.common.security.GlobalPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.securityMatcher("/actuator/**")
        .authorizeHttpRequests(requests -> requests.anyRequest().permitAll())
        .csrf(csrf -> csrf.disable())
        .build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .csrf(csrf -> csrf.spa())
        .formLogin(formLogin -> formLogin.disable())
        .logout(
            logout ->
                logout
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(
                        (request, response, authentication) ->
                            response.setStatus(HttpStatus.NO_CONTENT.value())))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/projects/*/invitations/**", "/invitations/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/users/recommendations")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/handles/availability")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/posts/recommendations")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/posts/drafts")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/projects/recommendations")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/projects/my")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/tags/recommendations", "/tags/unverified")
                    .authenticated()
                    .requestMatchers("/search/**")
                    .authenticated()
                    .requestMatchers(
                        HttpMethod.GET,
                        "/experiences/**",
                        "/companies/**",
                        "/posts/**",
                        "/comments/**",
                        "/users/**",
                        "/tags/{tagId}",
                        "/tags/{tagId}/posts",
                        "/team-building/**",
                        "/projects/**",
                        "/contests/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/providers/**", "/projects/**")
                    .authenticated()
                    .requestMatchers(
                        "/users/**",
                        "/profiles/me",
                        "/profiles/me/**",
                        "/media/**",
                        "/providers/my/**",
                        "/preferences/**")
                    .authenticated()
                    .requestMatchers(
                        "/jobs/**",
                        "/profiles/**",
                        "/auth/login",
                        "/auth/register",
                        "/auth/csrf",
                        "/auth/password-reset/**",
                        "/providers/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

    return http.build();
  }

  @Bean
  AuthenticationManager authenticationManager(
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authenticationProvider =
        new DaoAuthenticationProvider(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);

    return new ProviderManager(authenticationProvider);
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  @Bean
  MethodSecurityExpressionHandler methodSecurityExpressionHandler(
      GlobalPermissionEvaluator globalPermissionEvaluator) {
    DefaultMethodSecurityExpressionHandler expressionHandler =
        new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(globalPermissionEvaluator);

    return expressionHandler;
  }
}
