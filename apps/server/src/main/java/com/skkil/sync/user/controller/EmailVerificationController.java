package com.skkil.sync.user.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.dto.request.VerifyEmailRequest;
import com.skkil.sync.user.dto.response.SendVerificationEmailResponse;
import com.skkil.sync.user.service.EmailVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmailVerificationController {

  private final EmailVerificationService emailVerificationService;

  public EmailVerificationController(EmailVerificationService emailVerificationService) {
    this.emailVerificationService = emailVerificationService;
  }

  @PostMapping("/auth/email-verification/send")
  @ResponseStatus(HttpStatus.OK)
  public SendVerificationEmailResponse sendVerificationEmail(
      @AuthenticationPrincipal AuthenticatedUser user) {
    return emailVerificationService.sendVerificationEmail(user.userId());
  }

  @PostMapping("/auth/email-verification/verify")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void verifyEmail(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated VerifyEmailRequest request) {
    emailVerificationService.verifyEmail(user.userId(), request);
  }
}
