package com.skkil.sync.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.constant.Role;
import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.exception.UserAlreadyExistsException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

  @Mock private UserService userService;
  @Mock private UserRepository userRepository;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  @Test
  @DisplayName("authenticate 시 유효한 사용자는 인증 성공")
  void authenticate_validUser_success() {
    LoginRequest request = new LoginRequest("user@example.com", "password123");
    AuthenticatedUser authenticatedUser =
        new AuthenticatedUser(
            1L, "Test User", "user@example.com", "hashedPassword", Role.USER, true);

    when(userService.loadUserByUsername("user@example.com")).thenReturn(authenticatedUser);
    Authentication mockAuth = mock(Authentication.class);
    when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mockAuth);

    Authentication result = authService.authenticate(request);

    assertThat(result).isNotNull();
    verify(userService).loadUserByUsername("user@example.com");
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  @DisplayName("authenticate 시 이메일 미인증 사용자는 로그인 실패")
  void authenticate_unverifiedUser_throwBadCredentialsException() {
    LoginRequest request = new LoginRequest("user@example.com", "password123");
    AuthenticatedUser authenticatedUser =
        new AuthenticatedUser(
            1L, "Test User", "user@example.com", "hashedPassword", Role.USER, false);

    when(userService.loadUserByUsername("user@example.com")).thenReturn(authenticatedUser);

    assertThatThrownBy(() -> authService.authenticate(request))
        .isInstanceOf(BadCredentialsException.class);

    verify(authenticationManager, never()).authenticate(any(Authentication.class));
  }

  @Test
  @DisplayName("authenticate 시 사용자가 존재하지 않으면 UsernameNotFoundException 발생")
  void authenticate_userDoesNotExist_throwUsernameNotFoundException() {
    LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");

    when(userService.loadUserByUsername("nonexistent@example.com"))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThatThrownBy(() -> authService.authenticate(request))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("User not found");

    verify(authenticationManager, never()).authenticate(any(Authentication.class));
  }

  @Test
  @DisplayName("authenticate 시 비밀번호가 설정되지 않은 OAuth 사용자는 IllegalArgumentException 발생")
  void authenticate_oAuthUserWithNoPassword_throwIllegalArgumentException() {
    LoginRequest request = new LoginRequest("oauth@example.com", "password123");
    AuthenticatedUser authenticatedUser =
        new AuthenticatedUser(1L, "OAuth User", "oauth@example.com", null, Role.USER, true);

    when(userService.loadUserByUsername("oauth@example.com")).thenReturn(authenticatedUser);

    assertThatThrownBy(() -> authService.authenticate(request))
        .isInstanceOf(BadCredentialsException.class);

    verify(authenticationManager, never()).authenticate(any(Authentication.class));
  }

  @Test
  @DisplayName("registerUserAfterEmailVerification 시 새 사용자는 등록 성공")
  void registerUserAfterEmailVerification_newUser_success() {
    String encodedPassword = "encodedPassword";

    when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn(encodedPassword);
    when(userRepository.count()).thenReturn(1L);
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(1L);
              return user;
            });

    User result =
        authService.registerUserAfterEmailVerification("newuser@example.com", "password123");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertThat(savedUser)
        .hasFieldOrPropertyWithValue("email", "newuser@example.com")
        .hasFieldOrPropertyWithValue("hashedPassword", encodedPassword)
        .hasFieldOrPropertyWithValue("fullName", "")
        .hasFieldOrPropertyWithValue("emailVerified", true);

    verify(passwordEncoder).encode("password123");
    assertThat(result).isNotNull().hasFieldOrPropertyWithValue("email", "newuser@example.com");
  }

  @Test
  @DisplayName("registerUserAfterEmailVerification 시 이미 존재하는 이메일은 UserAlreadyExistsException 발생")
  void registerUserAfterEmailVerification_existingEmail_throwUserAlreadyExistsException() {
    User existingUser =
        User.builder()
            .email("existing@example.com")
            .fullName("Existing User")
            .hashedPassword("hashedPassword")
            .build();

    when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

    assertThatThrownBy(
            () ->
                authService.registerUserAfterEmailVerification(
                    "existing@example.com", "password123"))
        .isInstanceOf(UserAlreadyExistsException.class);

    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("registerUserAfterEmailVerification 시 비밀번호는 암호화되어 저장")
  void registerUserAfterEmailVerification_passwordIsEncoded() {
    String encodedPassword = "encodedHashedPassword";

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
    when(userRepository.count()).thenReturn(1L);
    when(userRepository.save(any(User.class)))
        .thenAnswer(
            invocation -> {
              User user = invocation.getArgument(0);
              user.setId(1L);
              return user;
            });

    authService.registerUserAfterEmailVerification("user@example.com", "plainPassword");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertThat(savedUser.getHashedPassword()).isEqualTo(encodedPassword);
  }
}
