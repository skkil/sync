package com.skkil.sync.user.service.oauth2;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.dto.request.RegisterRequest;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserOAuth2Account;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
    log.debug("Loading OIDC user from provider: {}", provider);

    OidcUser oidcUser = super.loadUser(userRequest);
    log.debug("OIDC user loaded: {}", oidcUser.getEmail());

    User user = processOAuth2User(provider, oidcUser);
    return AuthenticatedUser.builder()
        .userId(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .password(null)
        .role(user.getRole())
        .build();
  }

  @Transactional
  public User processOAuth2User(OAuth2Provider provider, OidcUser oidcUser) {
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

    if (user.getOAuth2Accounts().stream()
        .noneMatch(account -> provider.equals(account.getOAuth2Provider()))) {
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

    return user;
  }
}
