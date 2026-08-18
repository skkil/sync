package com.skkil.sync.auth.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.user.dto.request.ChangePasswordRequest;
import com.skkil.sync.user.dto.request.LoginRequest;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

/**
 * 비밀번호 변경·재설정이 HTTP 세션을 전부 무효화하는지(이후 요청 401) 검증한다.
 *
 * <p>살아 있는 WebSocket 소켓의 종료는 이 테스트의 범위 밖이다 — {@link WebSocketSessionTerminationIntegrationTests}가
 * 담당한다. 두 테스트가 함께 있어야 "비밀번호를 바꾸면 모든 자격 증명이 무효가 된다" 규칙이 채널 전체에서 검증된다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(properties = "app.seed.enabled=false")
class PasswordSessionInvalidationIntegrationTests {

  private static final String SESSION_COOKIE = "SESSION";
  private static final String PASSWORD = "password1234";

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapper jsonMapper;
  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private FindByIndexNameSessionRepository<? extends Session> sessionRepository;
  @Autowired private SessionInvalidationService sessionInvalidationService;

  private String email;

  @BeforeEach
  void setUp() {
    email = "session-test-" + System.nanoTime() + "@example.com";
    userRepository.save(
        User.builder()
            .email(email)
            .fullName("Session Test")
            .hashedPassword(passwordEncoder.encode(PASSWORD))
            .build());
  }

  @Test
  @DisplayName("로그인한 세션은 principal name 인덱스로 조회된다")
  void login_indexesSessionByPrincipalName() throws Exception {
    login();

    assertThat(sessionRepository.findByPrincipalName(email)).hasSize(1);
  }

  @Test
  @DisplayName("invalidateAllSessions 는 해당 사용자의 세션을 실제 저장소에서 모두 삭제한다")
  void invalidateAllSessions_deletesEverySessionFromTheStore() throws Exception {
    login();
    login();
    assertThat(sessionRepository.findByPrincipalName(email)).hasSize(2);

    sessionInvalidationService.invalidateAllSessions(email);

    assertThat(sessionRepository.findByPrincipalName(email)).isEmpty();
  }

  @Test
  @DisplayName("비밀번호 변경 시 기존 세션은 모두 만료되고 요청한 세션만 새로 발급된다")
  void changePassword_expiresEverySessionAndIssuesANewOne() throws Exception {
    Cookie first = login();
    Cookie second = login();
    assertThat(sessionRepository.findByPrincipalName(email)).hasSize(2);

    MvcResult result =
        mockMvc
            .perform(
                patch("/auth/password")
                    .cookie(first)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        jsonMapper.writeValueAsString(
                            new ChangePasswordRequest(PASSWORD, "newPassword1234"))))
            .andExpect(status().isNoContent())
            .andReturn();

    Cookie reissued = result.getResponse().getCookie(SESSION_COOKIE);
    assertThat(reissued).isNotNull();
    assertThat(reissued.getValue()).isNotEqualTo(first.getValue());

    assertThat(sessionRepository.findByPrincipalName(email)).hasSize(1);

    assertThat(statusOf(first)).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    assertThat(statusOf(second)).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    assertThat(statusOf(reissued)).isNotEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  private Cookie login() throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(new LoginRequest(email, PASSWORD))))
            .andExpect(status().isNoContent())
            .andReturn();

    Cookie cookie = result.getResponse().getCookie(SESSION_COOKIE);
    assertThat(cookie).isNotNull();

    return cookie;
  }

  private int statusOf(Cookie cookie) throws Exception {
    return mockMvc
        .perform(get("/profiles/me").cookie(cookie))
        .andReturn()
        .getResponse()
        .getStatus();
  }
}
