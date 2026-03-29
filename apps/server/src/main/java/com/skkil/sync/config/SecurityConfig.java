package com.skkil.sync.config;

import com.skkil.sync.common.security.EmailVerificationFilter;
import com.skkil.sync.common.security.GlobalPermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final EmailVerificationFilter emailVerificationFilter;
  @Bean
  @Order(2)
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .csrf(
            csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
        .formLogin(formLogin -> formLogin.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        .authorizeHttpRequests(
            requests ->
                requests
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers(
                        HttpMethod.GET,
                        "/experiences/**",
                        "/companies/**",
                        "/reflections/**",
                        "/users/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/providers/**")
                    .authenticated()
                    .requestMatchers("/users/**", "/profiles/me", "/media/**")
                    .authenticated()
                    .requestMatchers(
                        "/jobs/**",
                        "/profiles/**",
                        "/auth/login",
                        "/auth/register",
                        "/providers/**",
                        "/search/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception ->
                exception.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

    public SecurityConfig(EmailVerificationFilter emailVerificationFilter) {
        this.emailVerificationFilter = emailVerificationFilter;
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .csrf(
                        csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                                .ignoringRequestMatchers("/auth/register", "/auth/verify-email",
                                        "/auth/resend-verification-code"))
                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/experiences/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/providers/**")
                                .authenticated()
                                .requestMatchers("/users/**", "/profiles/me", "/media/**")
                                .authenticated()
                                .requestMatchers(
                                        "/profiles/**",
                                        "/auth/login",
                                        "/auth/register",
                                        "/auth/verify-email",
                                        "/auth/pending-email",
                                        "/auth/resend-verification-code",
                                        "/providers/**",
                                        "/search/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .addFilterAfter(emailVerificationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
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
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(globalPermissionEvaluator);

        return expressionHandler;
    }
}
