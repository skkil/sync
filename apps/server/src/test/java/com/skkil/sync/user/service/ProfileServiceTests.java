package com.skkil.sync.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.mapper.ProfileMapper;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTests {

  @Mock private UserRepository userRepository;
  @Mock private ProfileMapper profileMapper;

  @InjectMocks private ProfileService profileService;

  @Test
  void getProfile_userExists_returnProfile() {
    Long userId = 1L;
    User user = User.builder().email("user@email.com").fullName("Test User").bio("").build();
    user.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(profileMapper.toGetProfileResponseContacts(null)).thenReturn(null);

    GetProfileResponse response = profileService.getProfile(null, userId);

    assertThat(response)
        .isNotNull()
        .extracting("userId", "name", "email", "bio")
        .containsExactly("1", "Test User", "user@email.com", "");
  }

  @Test
  void getProfile_userNotFound_throwException() {
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> profileService.getProfile(null, userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
