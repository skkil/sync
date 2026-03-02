package com.skkil.sync.user.service.oauth2;

import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.dto.response.GetOAuth2AccountsResponse;
import com.skkil.sync.user.exception.UserCannotRemoveOAuthAccountException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserOAuth2Account;
import com.skkil.sync.user.repository.OAuth2AccountRepository;
import com.skkil.sync.user.repository.UserRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OAuth2Service {

  private final UserRepository userRepository;
  private final OAuth2AccountRepository oAuth2AccountRepository;

  public OAuth2Service(
      UserRepository userRepository, OAuth2AccountRepository oAuth2AccountRepository) {
    this.userRepository = userRepository;
    this.oAuth2AccountRepository = oAuth2AccountRepository;
  }

  @Transactional(readOnly = true)
  public GetOAuth2AccountsResponse getOAuth2Accounts(Long userId) {
    log.debug("Fetching OAuth2 accounts for user {}", userId);

    return new GetOAuth2AccountsResponse(
        oAuth2AccountRepository.findByUserId(userId).stream()
            .map(
                account -> new GetOAuth2AccountsResponse.OAuth2Account(account.getOAuth2Provider()))
            .toList());
  }

  @Transactional
  public void deleteOAuth2Account(Long userId, OAuth2Provider provider) {
    User user = userRepository.findById(userId).orElseThrow();
    if (user.getHashedPassword() == null) {
      throw new UserCannotRemoveOAuthAccountException("User does not have a password set.");
    }

    log.debug("Deleting OAuth2 account for user {} and provider {}", userId, provider);
    Optional<UserOAuth2Account> account =
        oAuth2AccountRepository.findByUserAndProvider(userId, provider);

    if (account.isEmpty()) {
      log.debug("No OAuth2 account found for user {} and provider {}", userId, provider);
      return;
    }

    oAuth2AccountRepository.delete(account.get());
  }
}
