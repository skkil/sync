package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.dto.request.VerifyEmailRequest;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import com.skkil.sync.user.service.EmailVerificationService;
import com.skkil.sync.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
class AuthController {

  private final AuthService authService;
  private final EmailVerificationService emailVerificationService;
  private final UserService userService;
  private final UserRepository userRepository;

  public AuthController(
      AuthService authService,
      EmailVerificationService emailVerificationService,
      UserService userService,
      UserRepository userRepository) {
    this.authService = authService;
    this.emailVerificationService = emailVerificationService;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @PostMapping("/auth/login")
  @ResponseStatus(HttpStatus.OK)
  public void login(HttpServletRequest http, @RequestBody @Validated LoginRequest request) {
    Authentication authentication = authService.authenticate(request);

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    HttpSession session = http.getSession(true);
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
  }

  @PostMapping("/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void register(HttpServletRequest http, @RequestBody @Validated RegisterRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new com.skkil.sync.user.exception.UserAlreadyExistsException(request.email());
    }

    // Store registration data in session
    HttpSession session = http.getSession(true);
    session.setAttribute("registration_email", request.email());
    session.setAttribute("registration_password", request.password());

    // Generate and send verification code
    emailVerificationService.createAndSendVerificationCode(request.email());
  }

  @PostMapping("/auth/verify-email")
  @ResponseStatus(HttpStatus.OK)
  public void verifyEmail(
      HttpServletRequest http, @RequestBody @Validated VerifyEmailRequest request) {
    // Verify the email code (returns verified email)
    String email = emailVerificationService.verifyEmailCode(request.email(), request.code());

    // Get registration data from session
    HttpSession session = http.getSession(false);
    if (session == null) {
      throw new IllegalStateException("등록 정보를 찾을 수 없습니다.");
    }

    String registrationEmail = (String) session.getAttribute("registration_email");
    String registrationPassword = (String) session.getAttribute("registration_password");

    if (registrationEmail == null || registrationPassword == null) {
      throw new IllegalStateException("등록 정보를 찾을 수 없습니다.");
    }

    if (!registrationEmail.equals(email)) {
      throw new IllegalStateException("이메일 주소가 일치하지 않습니다.");
    }

    authService.registerUserAfterEmailVerification(registrationEmail, registrationPassword);

    session.removeAttribute("registration_email");
    session.removeAttribute("registration_password");

    // Auto-login
    AuthenticatedUser principal = userService.loadUserByUsername(email);

    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Authentication authentication =
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            principal, principal.password(), principal.getAuthorities());
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);

    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
  }
}
