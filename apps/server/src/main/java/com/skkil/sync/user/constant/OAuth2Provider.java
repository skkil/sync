package com.skkil.sync.user.constant;

import lombok.Getter;

public enum OAuth2Provider {
  GOOGLE("google");

  @Getter private final String id;

  private OAuth2Provider(String id) {
    this.id = id;
  }

  public static OAuth2Provider from(String id) {
    for (OAuth2Provider provider : values()) {
      if (provider.getId().equals(id)) {
        return provider;
      }
    }

    throw new IllegalArgumentException("Unknown OAuthProvider ID: " + id);
  }
}
