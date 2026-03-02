package com.skkil.sync.user.service.oauth2;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.exception.OAuth2AccountCannotBeLinkedException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserOAuth2Account;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CustomOidcUserService extends OidcUserService {

  private final AuthService authService;
  private final UserRepository userRepository;

  public CustomOidcUserService(AuthService authService, UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Provider provider = OAuth2Provider.from(registrationId);
    log.debug("Received OIDC user request for provider: {}", provider);

    log.debug("Loading OIDC user from provider: {}", provider);
    OidcUser oidcUser = super.loadUser(userRequest);
    log.debug("OIDC user loaded: {}", oidcUser.getEmail());

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      log.debug("Authentication found in security context: {}", authentication.getName());

      if (authentication.getPrincipal() == null
          || !(authentication.getPrincipal() instanceof AuthenticatedUser)) {
        log.debug("Authentication principal is not an instance of AuthenticatedUser.");
        throw new IllegalStateException(
            "Authentication principal is not an instance of AuthenticatedUser");
      }

      AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

      if (!authenticatedUser.email().equals(oidcUser.getEmail())) {
        log.debug(
            "Authenticated user's email does not match OIDC user email. Cannot link accounts");
        throw new OAuth2AccountCannotBeLinkedException(
            "Authenticated user email does not match OIDC user email. Cannot link accounts.");
      }

      User user =
          userRepository
              .findByEmailWithOAuthAccounts(authenticatedUser.email())
              .orElseThrow(
                  () ->
                      new OAuth2AuthenticationException(
                          "Authenticated user not found in database."));

      linkOAuth2Account(user, provider, oidcUser);
      return authenticatedUser;
    }

    log.debug("No authentication found in security context. Processing OIDC user as new login.");

    User user = getOrCreateUser(provider, oidcUser);
    return AuthenticatedUser.builder()
        .userId(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .password(null)
        .role(user.getRole())
        .build();
  }

  @Transactional
  User getOrCreateUser(OAuth2Provider provider, OidcUser oidcUser) {
    String email = oidcUser.getEmail();

    Optional<User> optionalUser = userRepository.findByEmailWithOAuthAccounts(email);

    User user;

    if (optionalUser.isPresent()) {
      user = optionalUser.get();
    } else {
      log.debug("User not found with email: {}. Creating new user.", email);
      user = authService.registerUser(new RegisterRequest(email, null));
      user.setFullName(oidcUser.getFullName() == null ? "" : oidcUser.getFullName());
    }

    linkOAuth2Account(user, provider, oidcUser);
    return user;
  }

  @Transactional
  void linkOAuth2Account(User user, OAuth2Provider provider, OidcUser oidcUser) {
    if (user.getOAuth2Accounts().stream()
        .anyMatch(account -> provider.equals(account.getOAuth2Provider()))) {
      log.debug("User with email: {} already linked to provider: {}", user.getEmail(), provider);
      return;
    }

    if (oidcUser.getEmail() == null || !oidcUser.getEmail().equals(user.getEmail())) {
      log.debug("Email from OIDC user does not match the user's email. Cannot link accounts.");
      throw new OAuth2AccountCannotBeLinkedException(
          "Email from OIDC user does not match the user's email. Cannot link accounts.");
    }

    log.debug("Linking OAuth2 provider {} to user with email: {}", provider, user.getEmail());
    user.getOAuth2Accounts()
        .add(
            UserOAuth2Account.builder()
                .user(user)
                .oAuth2Provider(provider)
                .oAuth2ProviderUserId(oidcUser.getSubject())
                .build());
    user = userRepository.save(user);
  }
}
