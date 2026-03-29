package com.skkil.sync.user.service;

import com.skkil.sync.user.exception.InvalidTokenException;
import com.skkil.sync.user.exception.TokenExpiredException;
import com.skkil.sync.user.model.EmailVerificationToken;
import com.skkil.sync.user.repository.EmailVerificationTokenRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

  private static final int CODE_LENGTH = 6;
  private static final long EXPIRATION_MINUTES = 10;

  private final EmailVerificationTokenRepository tokenRepository;
  private final UserEmailService userEmailService;
  private final SecureRandom secureRandom = new SecureRandom();

  private String generateVerificationCode() {
    int bound = (int) Math.pow(10, CODE_LENGTH);
    int code = secureRandom.nextInt(bound);
    return String.format("%06d", code);
  }

  @Transactional
  public String verifyEmailCode(String email, String code) {
    EmailVerificationToken verificationToken =
        tokenRepository
            .findByEmailAndToken(email, code)
            .orElseThrow(() -> new InvalidTokenException("유효하지 않은 인증 코드입니다."));

    if (verificationToken.isExpired()) {
      throw new TokenExpiredException("인증 코드가 만료되었습니다.");
    }

    tokenRepository.delete(verificationToken);
    return email;
  }

  @Transactional
  public void sendVerificationCode(String email) {
    String code = generateVerificationCode();
    Instant expiresAt = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

    EmailVerificationToken verificationToken =
        tokenRepository
            .findByEmail(email)
            .map(
                existingToken -> {
                  existingToken.refresh(code, expiresAt);
                  return existingToken;
                })
            .orElseGet(() -> new EmailVerificationToken(email, code, expiresAt));

    tokenRepository.save(verificationToken);
    userEmailService.sendVerificationEmail(email, code);
  }
}
