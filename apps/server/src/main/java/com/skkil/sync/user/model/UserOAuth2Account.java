package com.skkil.sync.user.model;

import com.skkil.sync.common.domain.BaseEntity;
import com.skkil.sync.user.constant.OAuth2Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(
    name = "user_oauth2_accounts",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "oauth_provider"})})
@Getter
public class UserOAuth2Account extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "oauth_provider", nullable = false)
  private OAuth2Provider oAuth2Provider;

  @Column(name = "oauth_provider_user_id", nullable = false, length = 255)
  private String oAuth2ProviderUserId;

  protected UserOAuth2Account() {}

  @Builder
  public UserOAuth2Account(User user, OAuth2Provider oAuth2Provider, String oAuth2ProviderUserId) {
    this.user = user;
    this.oAuth2Provider = oAuth2Provider;
    this.oAuth2ProviderUserId = oAuth2ProviderUserId;
  }
}
