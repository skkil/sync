package com.skkil.sync.auth.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.user.dto.request.ChangePasswordRequest;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.service.AuthService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 세션 무효화가 살아 있는 WebSocket 소켓까지 끊는지 실제 포트 위에서 검증한다.
 *
 * <p>STOMP Principal은 핸드셰이크 시점에 고정되므로 세션 저장소 행 삭제만으로는 소켓이 살아남는다 — HTTP 경로의 401 전환은 {@link
 * PasswordSessionInvalidationIntegrationTests}가, 소켓 종료는 이 테스트가 담당한다. 레지스트리가 STOMP 프레임이 아니라 전송
 * 계층({@code afterConnectionEstablished})에 걸리므로, SockJS raw websocket 경로로 붙기만 하면 CONNECT 프레임 없이도
 * 추적·종료를 검증할 수 있다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestPropertySource(properties = {"app.seed.enabled=false", "app.websocket.enabled=true"})
class WebSocketSessionTerminationIntegrationTests {

  private static final String PASSWORD = "password1234";

  /** server.servlet.session.cookie.name — 실 HTTP에서는 소문자다(MockMvc는 서블릿 설정을 타지 않는다). */
  private static final String SESSION_COOKIE = "session";

  private static final int CREDENTIALS_INVALIDATED_CODE = CloseStatus.POLICY_VIOLATION.getCode();

  @LocalServerPort private int port;

  @Autowired private UserRepository userRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private SessionInvalidationService sessionInvalidationService;
  @Autowired private WebSocketSessionRegistry registry;
  @Autowired private AuthService authService;

  private final List<WebSocketSession> openedSessions = new java.util.ArrayList<>();

  private User user;
  private User otherUser;

  @BeforeEach
  void setUp() {
    user = saveUser();
    otherUser = saveUser();
  }

  @AfterEach
  void tearDown() {
    for (WebSocketSession session : openedSessions) {
      try {
        session.close();
      } catch (Exception ignored) {
        // 이미 닫힌 소켓 정리 시도는 무시한다.
      }
    }
  }

  @Test
  @DisplayName("세션 무효화는 해당 principal의 소켓만 끊는다")
  void invalidateAllSessions_closesOnlyTargetPrincipalSockets() throws Exception {
    ConnectedClient target = connectAs(user.getEmail());
    ConnectedClient bystander = connectAs(otherUser.getEmail());

    sessionInvalidationService.invalidateAllSessions(user.getEmail());

    assertThat(target.handler.closed.await(10, TimeUnit.SECONDS))
        .as("무효화된 principal의 소켓은 닫혀야 한다")
        .isTrue();
    assertThat(target.handler.closeStatus.getCode()).isEqualTo(CREDENTIALS_INVALIDATED_CODE);
    assertThat(registry.hasSessions(user.getEmail())).isFalse();

    // 다른 사용자는 영향받지 않는다 — 키 격리 회귀.
    assertThat(bystander.session.isOpen()).isTrue();
    assertThat(registry.hasSessions(otherUser.getEmail())).isTrue();
  }

  @Test
  @DisplayName("비밀번호를 변경하면 열려 있던 소켓이 끊긴다")
  void changePassword_closesLiveSockets() throws Exception {
    ConnectedClient client = connectAs(user.getEmail());

    authService.changePassword(
        user.getId(), new ChangePasswordRequest(PASSWORD, "newPassword1234"));

    assertThat(client.handler.closed.await(10, TimeUnit.SECONDS))
        .as("비밀번호 변경 후 기존 소켓은 닫혀야 한다")
        .isTrue();
    assertThat(client.handler.closeStatus.getCode()).isEqualTo(CREDENTIALS_INVALIDATED_CODE);
  }

  @Test
  @DisplayName("정상 종료된 소켓은 레지스트리에서 제거되고, 이후 무효화는 no-op이다")
  void normalClose_unregistersFromRegistry() throws Exception {
    ConnectedClient client = connectAs(user.getEmail());
    assertThat(registry.hasSessions(user.getEmail())).isTrue();

    client.session.close();

    awaitUnregistered(user.getEmail());
    sessionInvalidationService.invalidateAllSessions(user.getEmail());
  }

  private void awaitUnregistered(String email) throws InterruptedException {
    long deadline = System.currentTimeMillis() + 10_000;
    while (System.currentTimeMillis() < deadline) {
      if (!registry.hasSessions(email)) {
        return;
      }
      Thread.sleep(50);
    }
    throw new AssertionError("소켓 정상 종료 후에도 레지스트리에 세션이 남아 있다");
  }

  private ConnectedClient connectAs(String email) throws Exception {
    String sessionCookie = login(email);

    WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    headers.add(HttpHeaders.COOKIE, SESSION_COOKIE + "=" + sessionCookie);

    // SockJS raw websocket 전송 경로 — /ws/info 왕복과 STOMP 프레임 없이 소켓만 세운다.
    URI uri =
        URI.create(
            "ws://localhost:"
                + port
                + "/ws/0/"
                + UUID.randomUUID().toString().replace("-", "")
                + "/websocket");

    TrackingSocketHandler handler = new TrackingSocketHandler();
    WebSocketSession session =
        new StandardWebSocketClient().execute(handler, headers, uri).get(10, TimeUnit.SECONDS);
    openedSessions.add(session);

    return new ConnectedClient(session, handler);
  }

  /** CSRF 프라이밍 → 로그인으로 실제 SESSION 쿠키를 얻는다 (MockMvc가 아닌 실 HTTP). */
  private String login(String email) {
    RestClient client = RestClient.create("http://localhost:" + port);

    ResponseEntity<Void> csrfResponse =
        client.get().uri("/auth/csrf").retrieve().toBodilessEntity();
    String csrfToken = cookieValue(csrfResponse, "XSRF-TOKEN");

    ResponseEntity<Void> loginResponse =
        client
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.COOKIE, "XSRF-TOKEN=" + csrfToken)
            .header("X-XSRF-TOKEN", csrfToken)
            .body("{\"email\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}")
            .retrieve()
            .toBodilessEntity();

    return cookieValue(loginResponse, SESSION_COOKIE);
  }

  private static String cookieValue(ResponseEntity<?> response, String cookieName) {
    List<String> cookies = response.getHeaders().getOrEmpty(HttpHeaders.SET_COOKIE);
    return cookies.stream()
        .filter(cookie -> cookie.startsWith(cookieName + "="))
        .map(cookie -> cookie.substring(cookieName.length() + 1, cookie.indexOf(';')))
        .findFirst()
        .orElseThrow(() -> new AssertionError(cookieName + " 쿠키가 응답에 없다: " + cookies));
  }

  private User saveUser() {
    return userRepository.save(
        User.builder()
            .email("ws-termination-" + System.nanoTime() + "@example.com")
            .fullName("소켓 종료 테스트")
            .hashedPassword(passwordEncoder.encode(PASSWORD))
            .build());
  }

  private record ConnectedClient(WebSocketSession session, TrackingSocketHandler handler) {}

  private static final class TrackingSocketHandler extends TextWebSocketHandler {

    private final CountDownLatch closed = new CountDownLatch(1);
    private volatile CloseStatus closeStatus = CloseStatus.NO_STATUS_CODE;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
      this.closeStatus = status;
      this.closed.countDown();
    }
  }
}
