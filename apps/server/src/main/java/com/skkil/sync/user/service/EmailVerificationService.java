package com.skkil.sync.user.service;

import com.skkil.sync.common.integration.email.EmailService;
import com.skkil.sync.common.integration.email.dto.EmailMessage;
import com.skkil.sync.user.constant.EmailVerificationConstants;
import com.skkil.sync.user.dto.request.VerifyEmailRequest;
import com.skkil.sync.user.dto.response.SendVerificationEmailResponse;
import com.skkil.sync.user.exception.EmailAlreadyVerifiedException;
import com.skkil.sync.user.exception.EmailVerificationTokenExpiredException;
import com.skkil.sync.user.exception.EmailVerificationTokenInvalidException;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.EmailVerificationToken;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.EmailVerificationTokenRepository;
import com.skkil.sync.user.repository.UserRepository;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Slf4j
public class EmailVerificationService {

  private final UserRepository userRepository;
  private final EmailVerificationTokenRepository tokenRepository;
  private final EmailService emailService;
  private final SpringTemplateEngine templateEngine;
  private final Random random;

  public EmailVerificationService(
      UserRepository userRepository,
      EmailVerificationTokenRepository tokenRepository,
      EmailService emailService,
      SpringTemplateEngine templateEngine)
      throws NoSuchAlgorithmException {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.emailService = emailService;
    this.templateEngine = templateEngine;
    this.random = SecureRandom.getInstanceStrong();
  }

  @Transactional
  public SendVerificationEmailResponse sendVerificationEmail(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    if (user.isVerified()) {
      log.debug("User {} has already verified their email.", userId);
      throw new EmailAlreadyVerifiedException();
    }

    EmailVerificationToken token =
        tokenRepository
            .findByUser(user)
            .map(
                existing -> {
                  existing.refresh(generateVerificationToken());
                  return existing;
                })
            .orElseGet(
                () ->
                    EmailVerificationToken.builder()
                        .user(user)
                        .token(generateVerificationToken())
                        .build());

    tokenRepository.save(token);

    Context context = new Context();
    context.setVariable("token", token.getToken());
    context.setVariable(
        "expirationMinutes", EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_TTL.toMinutes());

    EmailMessage email =
        EmailMessage.builder()
            .to(user.getEmail())
            .subject("sync 이메일 인증")
            .text(templateEngine.process("email/verify-email", context))
            .build();

    log.debug("Sending email verification to user {}", userId);
    emailService
        .sendMessage(email)
        .exceptionally(
            e -> {
              log.error("Failed to send verification email to user {}", userId, e);
              return null;
            });

    return new SendVerificationEmailResponse(
        token.getExpiresAt(), EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_TTL.toSeconds());
  }

  @Transactional
  public void verifyEmail(Long userId, VerifyEmailRequest request) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    if (user.isVerified()) {
      throw new EmailAlreadyVerifiedException();
    }

    EmailVerificationToken verificationToken =
        tokenRepository
            .findByUserAndToken(user, request.token())
            .orElseThrow(EmailVerificationTokenInvalidException::new);

    if (verificationToken.isExpired()) {
      throw new EmailVerificationTokenExpiredException();
    }

    user.verifyEmail();
    userRepository.save(user);
    tokenRepository.delete(verificationToken);
  }

  private String generateVerificationToken() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_LENGTH; i++) {
      sb.append((char) ('A' + random.nextInt(26)));
    }

    return sb.toString();
  }
}
