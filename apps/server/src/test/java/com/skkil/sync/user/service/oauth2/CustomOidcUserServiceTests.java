package com.skkil.sync.user.service.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserOAuth2Account;
import com.skkil.sync.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@ExtendWith(MockitoExtension.class)
public class CustomOidcUserServiceTests {

  @InjectMocks private CustomOidcUserService customOidcUserService;

  @Mock private UserRepository userRepository;

  @Test
  @DisplayName("loadUser 시 존재하지 않는 OAuth2Provider면 IllegalArgumentException 발생")
  void loadUser_oAuth2ProviderDoesNotExist_throwIllegalArgumentException() {
    OidcUserRequest userRequest = mock(OidcUserRequest.class);
    ClientRegistration clientRegistration = mock(ClientRegistration.class);

    when(clientRegistration.getRegistrationId()).thenReturn("invalid_provider");
    when(userRequest.getClientRegistration()).thenReturn(clientRegistration);

    assertThatThrownBy(() -> customOidcUserService.loadUser(userRequest))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("processOAuth2User 시 계정이 없으면 새 계정 생성")
  void processOAuth2User_noAccountExists_createAccount() {
    OidcUser oidcUser = mock(OidcUser.class);
    OAuth2Provider provider = OAuth2Provider.GOOGLE;

    when(oidcUser.getEmail()).thenReturn("user@email.com");
    when(oidcUser.getFullName()).thenReturn("Test User");
    when(oidcUser.getSubject()).thenReturn("provider-user-id");

    when(userRepository.findByEmailWithOAuthAccounts("user@email.com"))
        .thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    User result = customOidcUserService.processOAuth2User(provider, oidcUser);

    assertThat(result)
        .hasFieldOrPropertyWithValue("email", "user@email.com")
        .hasFieldOrPropertyWithValue("fullName", "Test User");

    assertThat(result.getOAuth2Accounts())
        .hasSize(1)
        .allSatisfy(
            account -> {
              assertThat(account.getOAuth2Provider()).isEqualTo(provider);
              assertThat(account.getOAuth2ProviderUserId()).isEqualTo("provider-user-id");
            });
  }

  @Test
  @DisplayName("processOAuth2User 시 계정이 존재하지만 연결되지 않은 경우 계정 연결")
  void processOAuth2User_accountExistsButNotLinked_linksAccount() {
    OidcUser oidcUser = mock(OidcUser.class);
    OAuth2Provider provider = OAuth2Provider.GOOGLE;

    when(oidcUser.getEmail()).thenReturn("user@email.com");
    when(oidcUser.getSubject()).thenReturn("provider-user-id");

    User existingUser = User.builder().email("user@email.com").fullName("Test User").build();
    when(userRepository.findByEmailWithOAuthAccounts("user@email.com"))
        .thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    assertThat(existingUser.getOAuth2Accounts()).isEmpty();

    User result = customOidcUserService.processOAuth2User(provider, oidcUser);
    assertThat(result.getOAuth2Accounts())
        .hasSize(1)
        .allSatisfy(
            account -> {
              assertThat(account.getOAuth2Provider()).isEqualTo(provider);
              assertThat(account.getOAuth2ProviderUserId()).isEqualTo("provider-user-id");
            });
  }

  @Test
  @DisplayName("processOAuth2User 시 계정이 이미 연결된 경우 변경 없음")
  void processOAuth2User_accountAlreadyLinked_noChanges() {
    OidcUser oidcUser = mock(OidcUser.class);
    OAuth2Provider provider = OAuth2Provider.GOOGLE;
    when(oidcUser.getEmail()).thenReturn("user@email.com");

    User existingUser = User.builder().email("user@email.com").fullName("Test User").build();
    existingUser
        .getOAuth2Accounts()
        .add(
            UserOAuth2Account.builder()
                .user(existingUser)
                .oAuth2Provider(provider)
                .oAuth2ProviderUserId("provider-user-id")
                .build());

    when(userRepository.findByEmailWithOAuthAccounts("user@email.com"))
        .thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

    User result = customOidcUserService.processOAuth2User(provider, oidcUser);

    assertThat(result)
        .hasFieldOrPropertyWithValue("email", "user@email.com")
        .hasFieldOrPropertyWithValue("fullName", "Test User");

    assertThat(result.getOAuth2Accounts())
        .hasSize(1)
        .allSatisfy(
            account -> {
              assertThat(account.getOAuth2Provider()).isEqualTo(provider);
              assertThat(account.getOAuth2ProviderUserId()).isEqualTo("provider-user-id");
            });
  }
}
