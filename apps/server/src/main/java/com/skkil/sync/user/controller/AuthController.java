package com.skkil.sync.user.controller;

import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.dto.request.VerifyEmailRequest;
import com.skkil.sync.user.dto.response.PendingEmailResponse;
import com.skkil.sync.user.exception.UserAlreadyExistsException;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import com.skkil.sync.user.service.EmailVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class AuthController {

  private static final String REGISTRATION_EMAIL = "registration_email";
  private static final String REGISTRATION_PASSWORD = "registration_password";

  private final AuthService authService;
  private final EmailVerificationService emailVerificationService;
  private final UserRepository userRepository;

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
    if (userRepository.findByEmail(request.email()).isPresent()) {
      throw new UserAlreadyExistsException(request.email());
    }

    HttpSession session = http.getSession(true);
    session.setAttribute(REGISTRATION_EMAIL, request.email());
    session.setAttribute(REGISTRATION_PASSWORD, request.password());

    emailVerificationService.sendVerificationCode(request.email());
  }

  @GetMapping("/auth/pending-email")
  @ResponseStatus(HttpStatus.OK)
  public PendingEmailResponse getPendingEmail(HttpServletRequest http) {
    return new PendingEmailResponse(getRegistrationEmail(http.getSession(false)));
  }

  @PostMapping("/auth/verify-email")
  @ResponseStatus(HttpStatus.OK)
  public void verifyEmail(
      HttpServletRequest http, @RequestBody @Validated VerifyEmailRequest request) {
    HttpSession session = getRequiredSession(http);

    String registrationEmail = getRegistrationEmail(session);
    String registrationPassword = getRegistrationPassword(session);

    emailVerificationService.verifyEmailCode(registrationEmail, request.code());
    authService.registerUserAfterEmailVerification(registrationEmail, registrationPassword);

    session.removeAttribute(REGISTRATION_EMAIL);
    session.removeAttribute(REGISTRATION_PASSWORD);
  }

  @PostMapping("/auth/resend-verification-code")
  @ResponseStatus(HttpStatus.OK)
  public void resendVerificationCode(HttpServletRequest http) {
    HttpSession session = getRequiredSession(http);
    emailVerificationService.sendVerificationCode(getRegistrationEmail(session));
  }

  private HttpSession getRequiredSession(HttpServletRequest http) {
    HttpSession session = http.getSession(false);
    if (session == null) {
      throw new IllegalStateException("등록 정보를 찾을 수 없습니다.");
    }
    return session;
  }

  private String getRegistrationEmail(HttpSession session) {
    String email = (String) session.getAttribute(REGISTRATION_EMAIL);
    if (email == null) {
      throw new IllegalStateException("인증할 이메일 정보를 찾을 수 없습니다.");
    }
    return email;
  }

  private String getRegistrationPassword(HttpSession session) {
    String password = (String) session.getAttribute(REGISTRATION_PASSWORD);
    if (password == null) {
      throw new IllegalStateException("등록 정보를 찾을 수 없습니다.");
    }
    return password;
  }
}
