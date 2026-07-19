package com.skkil.sync.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileAssemblerTests {

  @SuppressWarnings("UnusedVariable")
  @Mock
  private ProfileMapper profileMapper;

  @InjectMocks private ProfileAssembler profileAssembler;

  @Test
  @DisplayName("[toGetProfileResponse] 본인 프로필 조회 시 isOnboarded/isEmailVerified가 노출된다")
  void toGetProfileResponse_authenticatedUser_exposesOnboardingFields() {
    User user = createOnboardedUser();

    GetProfileResponse response = profileAssembler.toGetProfileResponse(user, null, false, true);

    assertThat(response.isOnboarded()).isTrue();
    assertThat(response.isEmailVerified()).isTrue();
  }

  @Test
  @DisplayName("[toGetProfileResponse] 타인 프로필 조회 시 isOnboarded/isEmailVerified는 null이다")
  void toGetProfileResponse_otherUser_hidesOnboardingFields() {
    User user = createOnboardedUser();

    GetProfileResponse response = profileAssembler.toGetProfileResponse(user, null, false, false);

    assertThat(response.isOnboarded()).isNull();
    assertThat(response.isEmailVerified()).isNull();
  }

  private static User createOnboardedUser() {
    User user = new User(1L);
    user.updateHandle("testuser1");
    user.verifyEmail();
    user.onboard();

    return user;
  }
}
