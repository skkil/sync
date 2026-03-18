package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "email_verification_tokens")
@SQLDelete(sql = "UPDATE email_verification_tokens SET deleted_at = NOW() WHERE id = ?")
@Getter
public class EmailVerificationToken extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @Column(name = "email", nullable = true, length = 255)
  private String email;

  @Column(name = "token", nullable = false, unique = true, length = 255)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "verified_at")
  private Instant verifiedAt;

  @Column(name = "used", nullable = false)
  private boolean used;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected EmailVerificationToken() {}

  public EmailVerificationToken(User user, String token, Instant expiresAt) {
    this.user = user;
    this.email = user != null ? user.getEmail() : null;
    this.token = token;
    this.expiresAt = expiresAt;
    this.used = false;
  }

  public EmailVerificationToken(String email, String token, Instant expiresAt) {
    this.user = null;
    this.email = email;
    this.token = token;
    this.expiresAt = expiresAt;
    this.used = false;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(Instant.now());
  }

  public boolean isUsable() {
    return !used && verifiedAt == null && !isExpired();
  }

  public void markAsUsed() {
    this.used = true;
    this.verifiedAt = Instant.now();
  }
}
