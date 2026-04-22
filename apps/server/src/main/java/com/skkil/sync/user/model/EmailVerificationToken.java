package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.constant.EmailVerificationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "email_verification_tokens")
@Getter
public class EmailVerificationToken extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true, nullable = false)
  private User user;

  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  protected EmailVerificationToken() {}

  @Builder
  public EmailVerificationToken(User user, String token) {
    this.user = user;
    this.token = token;
    this.expiresAt = Instant.now().plus(EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_TTL);
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiresAt);
  }

  public void refresh(String token) {
    this.token = token;
    this.expiresAt = Instant.now().plus(EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_TTL);
  }
}
