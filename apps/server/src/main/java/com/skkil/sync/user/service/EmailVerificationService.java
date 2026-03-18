package com.skkil.sync.user.service;

import com.skkil.sync.user.exception.InvalidTokenException;
import com.skkil.sync.user.exception.TokenExpiredException;
import com.skkil.sync.user.model.EmailVerificationToken;
import com.skkil.sync.user.repository.EmailVerificationTokenRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class EmailVerificationService {

  private static final int CODE_LENGTH = 6;
  private static final long EXPIRATION_MINUTES = 10;

  private final EmailVerificationTokenRepository tokenRepository;
  private final EmailService emailService;
  private final SecureRandom secureRandom = new SecureRandom();

  public EmailVerificationService(
      EmailVerificationTokenRepository tokenRepository, EmailService emailService) {
    this.tokenRepository = tokenRepository;
    this.emailService = emailService;
  }

  private String generateVerificationCode() {
    int bound = (int) Math.pow(10, CODE_LENGTH);
    int code = secureRandom.nextInt(bound);
    return String.format("%06d", code);
  }

  @Transactional
  public void createAndSendVerificationCode(String email) {
    invalidatePreviousTokens(email);

    String code = generateVerificationCode();
    Instant expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

    EmailVerificationToken verificationToken = new EmailVerificationToken(email, code, expiresAt);

    tokenRepository.save(verificationToken);
    emailService.sendVerificationEmail(email, code);

    log.info("Created verification code for email {}", email);
  }

  @Transactional
  public String verifyEmailCode(String email, String code) {
    EmailVerificationToken verificationToken =
        tokenRepository
            .findByEmailAndToken(email, code)
            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 인증 코드입니다."));

    if (!verificationToken.isUsable()) {
      if (verificationToken.isExpired()) {
        throw new TokenExpiredException("인증 코드가 만료되었습니다.");
      }
      throw new InvalidTokenException("이미 사용된 인증 코드입니다.");
    }

    verificationToken.markAsUsed();

    log.info("Email verification code verified for email {}", email);
    return email;
  }

  @Transactional
  public void resendVerificationCode(String email) {
    invalidatePreviousTokens(email);

    String code = generateVerificationCode();
    Instant expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

    EmailVerificationToken verificationToken = new EmailVerificationToken(email, code, expiresAt);

    tokenRepository.save(verificationToken);
    emailService.sendVerificationEmail(email, code);

    log.info("Resent verification code for email {}", email);
  }

  private void invalidatePreviousTokens(String email) {
    List<EmailVerificationToken> tokens = tokenRepository.findAllByEmailAndUsedFalse(email);

    for (EmailVerificationToken token : tokens) {
      token.markAsUsed();
    }
  }
}
