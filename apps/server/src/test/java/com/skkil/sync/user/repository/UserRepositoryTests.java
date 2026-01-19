package com.skkil.sync.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.config.TestcontainersConfig;
import com.skkil.sync.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestcontainersConfig.class)
class UserRepositoryTests {

  @Autowired private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("User 저장 시 문제 없으면 필요한 필드들 생성")
  void createUser_noConstraintsViolated_requiredFieldsCreated() {
    User user = User.builder().email("user@email.com").fullName("Full Name").build();
    user = userRepository.save(user);
    assertThat(user.getId()).isNotNull();

    User foundUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(foundUser.getCreatedAt()).isNotNull();
    assertThat(foundUser.getUpdatedAt()).isNotNull();
  }
}
