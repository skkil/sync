package com.skkil.sync.auth;

import com.skkil.sync.user.constant.Role;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Builder
public record AuthenticatedUser(
    Long userId, String fullName, String email, String password, Role role, boolean emailVerified)
    implements UserDetails, OidcUser {

  public AuthenticatedUser(Long userId) {
    this(userId, null, null, null, null);
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public @Nullable String getPassword() {
    return password;
  }

  @Override
  public String getName() {
    return email;
  }

  @Override
  public Map<String, Object> getClaims() {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return null;
  }

  @Override
  public OidcIdToken getIdToken() {
    return null;
  }

  @Override
  public List<? extends GrantedAuthority> getAuthorities() {
    if (role == null) {
      return List.of();
    }

    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName());
    return List.of(authority);
  }

  public boolean isAdmin() {
    return role == Role.ADMIN;
  }
}
