package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;

@Entity
@Table(name = "email_verification_tokens")
@Getter
public class EmailVerificationToken extends BaseEntity {
  @Column(name = "email", nullable = true, length = 255)
  private String email;

  @Column(name = "token", nullable = false, unique = true, length = 255)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  protected EmailVerificationToken() {}

  public EmailVerificationToken(User user, String token, Instant expiresAt) {
    this.email = user != null ? user.getEmail() : null;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public EmailVerificationToken(String email, String token, Instant expiresAt) {
    this.email = email;
    this.token = token;
    this.expiresAt = expiresAt;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(Instant.now());
  }

  public void refresh(String newToken, Instant newExpiresAt) {
    this.token = newToken;
    this.expiresAt = newExpiresAt;
  }
}
