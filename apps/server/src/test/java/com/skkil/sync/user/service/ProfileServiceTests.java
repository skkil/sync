package com.skkil.sync.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.mapper.ProfileMapper;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTests {

  @Mock private UserRepository userRepository;
  @Mock private UserRelationshipService userRelationshipService;
  @Mock private ProfileMapper profileMapper;

  @InjectMocks private ProfileService profileService;

  @Test
  @DisplayName("[getProfileById] 사용자가 비활성화되어 있고 요청자가 사용자가 아닌 경우 UserNotFoundException 예외 발생")
  void getProfileById_userIsDisabledAndNotRequester_throwUserNotFound() {
    Long userId = 1L, requesterId = 2L;

    AuthenticatedUser requester = new AuthenticatedUser(requesterId);
    User user = new User(userId);

    assertThat(user.isEnabled()).isFalse();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> profileService.getProfileById(null, userId))
        .isInstanceOf(UserNotFoundException.class);

    assertThatThrownBy(() -> profileService.getProfileById(requester, userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("[getProfileById] 사용자가 비활성화되어 있고 요청자가 사용자 본인인 경우 예외 발생하지 않음")
  void getProfileById_userIsDisabledButRequester_throwNoException() {
    Long userId = 1L, requesterId = 1L;

    AuthenticatedUser requester = new AuthenticatedUser(requesterId);
    User user = new User(userId);

    assertThat(user.isEnabled()).isFalse();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(profileMapper.toGetProfileResponseContacts(null)).thenReturn(null);
    when(userRelationshipService.isFollowing(requesterId, userId)).thenReturn(false);

    assertThatCode(() -> profileService.getProfileById(requester, userId))
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("[getProfileById] 사용자를 찾을 수 없는 경우 UserNotFoundException 예외 발생")
  void getProfileById_userNotFound_throwUserNotFound() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> profileService.getProfileById(null, userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
