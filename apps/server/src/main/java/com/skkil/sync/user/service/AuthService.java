package com.skkil.sync.user.service;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.event.UserRegisteredEvent;
import com.skkil.sync.user.exception.UserAlreadyExistsException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

  private final UserService userService;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  public AuthService(
      UserService userService,
      UserRepository userRepository,
      AuthenticationManager authenticationManager,
      PasswordEncoder passwordEncoder,
      ApplicationEventPublisher eventPublisher) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
  }

  @Transactional(readOnly = true)
  public Authentication authenticate(LoginRequest request) {
    AuthenticatedUser principal = userService.loadUserByUsername(request.email());
    if (principal.password() == null) {
      log.debug("Authentication failed, user has no password set");
      throw new BadCredentialsException("");
    }

    Authentication authentication =
        UsernamePasswordAuthenticationToken.authenticated(
            principal, request.password(), principal.getAuthorities());
    authenticationManager.authenticate(authentication);

    return authentication;
  }

  @Transactional
  public void registerUser(RegisterRequest request) {
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new UserAlreadyExistsException(request.email());
    }

    User user =
        User.builder()
            .fullName("")
            .email(request.email())
            .hashedPassword(passwordEncoder.encode(request.password()))
            .build();
    user = userRepository.save(user);
    log.info("Registered new user with id {}", user.getId());

    eventPublisher.publishEvent(new UserRegisteredEvent(user.getId()));
  }
}
