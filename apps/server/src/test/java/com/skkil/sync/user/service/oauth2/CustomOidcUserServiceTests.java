package com.skkil.sync.user.service.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.exception.OAuth2AccountCannotBeLinkedException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@ExtendWith(MockitoExtension.class)
public class CustomOidcUserServiceTests {

  @InjectMocks private CustomOidcUserService customOidcUserService;

  @Mock private UserRepository userRepository;

  @Test
  void getOrCreateUser_userNotExists_createNewUser() {
    String email = "newuser@email.com";
    String fullName = "New User";
    OidcUser oidcUser = createMockOidcUser(email, fullName);

    User newUser =
        User.builder().email(email).fullName(fullName).hashedPassword(null).bio("").build();
    newUser.setId(1L);
    newUser.verifyEmail();

    when(userRepository.findByEmailWithOAuthAccounts(email)).thenReturn(Optional.empty());
    when(userRepository.count()).thenReturn(1L);
    when(userRepository.save(any(User.class))).thenReturn(newUser);

    User result = customOidcUserService.getOrCreateUser(OAuth2Provider.GOOGLE, oidcUser);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getFullName()).isEqualTo(fullName);
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.isEmailVerified()).isTrue();
    verify(userRepository).findByEmailWithOAuthAccounts(email);
    verify(userRepository, atLeastOnce()).save(any());
  }

  @Test
  void getOrCreateUser_userExists_returnExistingUser() {
    String email = "existinguser@email.com";
    String fullName = "Existing User";
    OidcUser oidcUser = createMockOidcUser(email, fullName);

    User existingUser =
        User.builder()
            .email(email)
            .fullName(fullName)
            .hashedPassword("hashedPassword")
            .bio("Bio")
            .build();
    existingUser.setId(2L);

    when(userRepository.findByEmailWithOAuthAccounts(email)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(existingUser);

    User result = customOidcUserService.getOrCreateUser(OAuth2Provider.GOOGLE, oidcUser);

    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getId()).isEqualTo(2L);
    verify(userRepository).findByEmailWithOAuthAccounts(email);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void linkOAuth2Account_emailMatches_linkAccount() {
    String email = "user@email.com";
    OidcUser oidcUser = createMockOidcUser(email, "User Name");

    User user =
        User.builder()
            .email(email)
            .fullName("User Name")
            .hashedPassword("password")
            .bio("")
            .build();
    user.setId(1L);

    when(userRepository.save(any(User.class))).thenReturn(user);

    customOidcUserService.linkOAuth2Account(user, OAuth2Provider.GOOGLE, oidcUser);

    assertThat(user.getOAuth2Accounts()).hasSize(1);
    assertThat(user.getOAuth2Accounts().get(0).getOAuth2Provider())
        .isEqualTo(OAuth2Provider.GOOGLE);
    verify(userRepository).save(user);
  }

  @Test
  void linkOAuth2Account_emailDoesNotMatch_throwException() {
    String userEmail = "user@email.com";
    String oidcEmail = "different@email.com";
    OidcUser oidcUser = createMockOidcUser(oidcEmail, "Different User");

    User user =
        User.builder()
            .email(userEmail)
            .fullName("User Name")
            .hashedPassword("password")
            .bio("")
            .build();
    user.setId(1L);

    assertThatThrownBy(
            () -> customOidcUserService.linkOAuth2Account(user, OAuth2Provider.GOOGLE, oidcUser))
        .isInstanceOf(OAuth2AccountCannotBeLinkedException.class);
  }

  @Test
  void linkOAuth2Account_accountAlreadyLinked_doNothing() {
    String email = "user@email.com";
    OidcUser oidcUser = createMockOidcUser(email, "User Name");

    User user =
        User.builder()
            .email(email)
            .fullName("User Name")
            .hashedPassword("password")
            .bio("")
            .build();
    user.setId(1L);

    when(userRepository.save(any(User.class))).thenReturn(user);

    customOidcUserService.linkOAuth2Account(user, OAuth2Provider.GOOGLE, oidcUser);
    int initialSize = user.getOAuth2Accounts().size();

    customOidcUserService.linkOAuth2Account(user, OAuth2Provider.GOOGLE, oidcUser);

    assertThat(user.getOAuth2Accounts()).hasSize(initialSize);
  }

  private OidcUser createMockOidcUser(String email, String fullName) {
    OidcIdToken idToken =
        OidcIdToken.withTokenValue("token")
            .subject("oidc-user-id-123")
            .claim("email", email)
            .claim("name", fullName)
            .build();

    OidcUserInfo userInfo =
        OidcUserInfo.builder().subject("oidc-user-id-123").email(email).name(fullName).build();

    return new DefaultOidcUser(List.of(new SimpleGrantedAuthority("ROLE_USER")), idToken, userInfo);
  }
}
