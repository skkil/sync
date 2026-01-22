package com.skkil.sync.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.config.TestcontainersConfig;
import com.skkil.sync.user.constant.OAuth2Provider;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserOAuth2Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class})
class UserRepositoryTests {

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("User 생성 시 createdAt, updatedAt 필드 생성")
  void createUser_createdAtUpdatedAtFieldsSet() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    user = userRepository.save(user);
    assertThat(user.getId()).isNotNull();

    User foundUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(foundUser.getCreatedAt()).isNotNull();
    assertThat(foundUser.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("User 생성 시 oAuth2Accounts 필드가 null이 아님")
  void createUser_oAuth2AccountsIsNotNull() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    assertThat(user.getOAuth2Accounts()).isNotNull();
  }

  @Test
  @DisplayName("User 생성 시 email이 중복된 이메일이면 예외 발생")
  void createUser_duplicateEmail_throwsException() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    userRepository.save(user);

    User user2 = User.builder().email("user@email.com").fullName("Full Name").build();
    assertThatThrownBy(() -> userRepository.save(user2)).isInstanceOf(Exception.class);
  }

  @Test
  @DisplayName("User 생성 시 email이나 fullName이 null이면 예외 발생")
  void createUser_nullRequiredFields_throwsException() {
    User userMissingEmail = User.builder().fullName("Full Name").build();
    assertThatThrownBy(() -> userRepository.save(userMissingEmail)).isInstanceOf(Exception.class);

    User userMissingFullName = User.builder().email("email@email.com").build();
    assertThatThrownBy(() -> userRepository.save(userMissingFullName))
        .isInstanceOf(Exception.class);
  }

  @Test
  @DisplayName("User 삭제 시 deletedAt 필드 값 설정")
  void deleteUser_deletedAtSet() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    user = userRepository.save(user);
    assertThat(user.getDeletedAt()).isNull();

    userRepository.delete(user);
    userRepository.flush();

    User foundUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(foundUser.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("OAuth2Account 추가 시 정상적으로 추가됨")
  void addOAuth2Account_accountAddedSuccessfully() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    assertThat(user.getOAuth2Accounts()).isNotNull().isEmpty();

    user.getOAuth2Accounts()
        .add(
            UserOAuth2Account.builder()
                .user(user)
                .oAuth2Provider(OAuth2Provider.GOOGLE)
                .oAuth2ProviderUserId("google-user-id")
                .build());
    user = userRepository.save(user);

    User foundUser = userRepository.findByEmailWithOAuthAccounts(user.getEmail()).orElseThrow();
    assertThat(foundUser.getOAuth2Accounts())
        .hasSize(1)
        .allSatisfy(
            account -> {
              assertThat(account.getOAuth2Provider()).isEqualTo(OAuth2Provider.GOOGLE);
              assertThat(account.getOAuth2ProviderUserId()).isEqualTo("google-user-id");
            });
  }
}
