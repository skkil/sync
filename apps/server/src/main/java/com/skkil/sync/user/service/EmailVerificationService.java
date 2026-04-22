package com.skkil.sync.user.service;

import com.skkil.sync.common.integration.email.EmailService;
import com.skkil.sync.common.integration.email.dto.EmailMessage;
import com.skkil.sync.user.constant.EmailVerificationConstants;
import com.skkil.sync.user.exception.EmailVerificationTokenExpiredException;
import com.skkil.sync.user.exception.EmailVerificationTokenInvalidException;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.EmailVerificationToken;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.EmailVerificationTokenRepository;
import com.skkil.sync.user.repository.UserRepository;
import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
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
      throws Exception {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.emailService = emailService;
    this.templateEngine = templateEngine;
    this.random = SecureRandom.getInstanceStrong();
  }

  @Transactional
  public void sendVerificationEmail(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

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

    emailService.sendMessage(email);
  }

  @Transactional
  public void verifyEmail(Long userId, String token) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    EmailVerificationToken verificationToken =
        tokenRepository
            .findByUserAndToken(user, token)
            .orElseThrow(() -> new EmailVerificationTokenInvalidException());

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
