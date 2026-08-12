package com.skkil.sync.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.skkil.sync.auth.agent.AgentScopes;
import com.skkil.sync.auth.agent.AuthorizedAgentClient;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.common.security.WithAuthenticatedUser;
import com.skkil.sync.mcp.CreatePostTool;
import com.skkil.sync.post.dto.request.CreateAgentPostRequest;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.service.AgentPostService;
import jakarta.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 에이전트 기능은 나머지 테스트에서 꺼져 있다(build.gradle 참고). 여기서만 켜고 전체 컨텍스트를 띄워, 인가 서버와 리소스 서버가 실제로 조립되는지와 그 위에서
 * 쉽게 어긋나는 지점들을 확인한다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@TestPropertySource(
    properties = {
      "app.seed.enabled=false",
      "app.agent.enabled=true",
      "app.agent.chatgpt-redirect-uri=https://chatgpt.com/connector/oauth/test-callback",
      "spring.ai.mcp.server.enabled=true"
    })
class AgentContextIntegrationTests {

  private static final String CODE_VERIFIER = "sync-agent-test-code-verifier-0123456789";
  private static final String CLIENT_ID = "claude-code";
  private static final String CHATGPT_CLIENT_ID = "chatgpt";
  private static final String CHATGPT_REDIRECT_URI =
      "https://chatgpt.com/connector/oauth/test-callback";
  private static final String PRINCIPAL_NAME = "email@email.com";
  private static final String REDIRECT_URI = "http://localhost:49152/callback";

  @Autowired private MockMvc mockMvc;
  @Autowired private AgentPostService agentPostService;
  @Autowired private CreatePostTool createPostTool;
  @Autowired private RegisteredClientRepository registeredClientRepository;
  @Autowired private OAuth2AuthorizationConsentService authorizationConsentService;
  @Autowired private OAuth2AuthorizationService authorizationService;

  @AfterEach
  void revokeConsent() {
    revokeConsent(CLIENT_ID);
    revokeConsent(CHATGPT_CLIENT_ID);
  }

  private void revokeConsent(String clientId) {
    RegisteredClient client = registeredClientRepository.findByClientId(clientId);
    if (client == null) {
      return;
    }

    OAuth2AuthorizationConsent consent =
        authorizationConsentService.findById(client.getId(), PRINCIPAL_NAME);

    if (consent != null) {
      authorizationConsentService.remove(consent);
    }
  }

  @Test
  @DisplayName("인가 서버 메타데이터가 posts:draft 스코프와 함께 노출된다")
  void authorizationServerMetadataIsServed() throws Exception {
    mockMvc
        .perform(get("/.well-known/oauth-authorization-server"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authorization_endpoint").exists())
        .andExpect(jsonPath("$.token_endpoint").exists())
        .andExpect(jsonPath("$.jwks_uri").exists())
        .andExpect(jsonPath("$.token_endpoint_auth_methods_supported[?(@ == 'none')]").exists())
        .andExpect(jsonPath("$.scopes_supported[?(@ == 'posts:draft')]").exists());
  }

  @Test
  @DisplayName("토큰 없는 MCP 요청은 401 과 함께 인가 서버를 알려 준다")
  void mcpWithoutTokenChallenges() throws Exception {
    mockMvc
        .perform(get("/mcp"))
        .andExpect(status().isUnauthorized())
        .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE));
  }

  @Test
  @DisplayName("로그인하지 않은 인가 요청은 원래 주소를 redirect 파라미터에 담아 로그인 화면으로 보낸다")
  void unauthenticatedAuthorizeCarriesResumeTarget() throws Exception {
    mockMvc
        .perform(authorize("http://127.0.0.1:49152/callback").accept(MediaType.TEXT_HTML))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/auth/login?redirect=*oauth2*authorize*"));
  }

  @Test
  @DisplayName("포트가 붙은 localhost 콜백도 등록된 리다이렉트로 인정한다")
  @WithAuthenticatedUser
  void localhostRedirectUriWithEphemeralPortIsAccepted() throws Exception {
    mockMvc
        .perform(authorize("http://localhost:49152/callback"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @DisplayName("ChatGPT 공개 클라이언트는 설정된 콜백과 PKCE 정책으로 등록된다")
  void chatGptPublicClientIsRegistered() {
    RegisteredClient client = registeredClientRepository.findByClientId(CHATGPT_CLIENT_ID);

    assertThat(client).isNotNull();
    assertThat(client.getClientAuthenticationMethods())
        .containsExactly(ClientAuthenticationMethod.NONE);
    assertThat(client.getAuthorizationGrantTypes())
        .containsExactlyInAnyOrder(
            AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN);
    assertThat(client.getRedirectUris()).contains(CHATGPT_REDIRECT_URI);
    assertThat(client.getScopes()).containsExactly(AgentScopes.POSTS_DRAFT);
    assertThat(client.getClientSettings().isRequireProofKey()).isTrue();
    assertThat(client.getClientSettings().isRequireAuthorizationConsent()).isTrue();
  }

  @Test
  @DisplayName("설정된 ChatGPT 콜백은 인가 요청에 사용할 수 있다")
  @WithAuthenticatedUser
  void chatGptRedirectUriIsAccepted() throws Exception {
    mockMvc
        .perform(authorize(CHATGPT_CLIENT_ID, CHATGPT_REDIRECT_URI))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  @DisplayName("ChatGPT 공개 클라이언트도 PKCE 인가 코드를 토큰으로 교환한다")
  @WithAuthenticatedUser
  void chatGptPublicClientExchangesAuthorizationCode() throws Exception {
    grantConsent(CHATGPT_CLIENT_ID);

    mockMvc
        .perform(exchangeAuthorizationCode(CHATGPT_CLIENT_ID, CHATGPT_REDIRECT_URI, CODE_VERIFIER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").exists())
        .andExpect(jsonPath("$.refresh_token").exists());
  }

  @Test
  @DisplayName("설정되지 않은 ChatGPT 콜백은 거부한다")
  @WithAuthenticatedUser
  void unregisteredChatGptRedirectUriIsRejected() throws Exception {
    mockMvc
        .perform(authorize(CHATGPT_CLIENT_ID, "https://chatgpt.com/connector/oauth/wrong"))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "http://chatgpt.com/connector/oauth/test-callback",
        "https://example.com/connector/oauth/test-callback",
        "https://chatgpt.com/connector/oauth/",
        "https://chatgpt.com/connector/oauth/test-callback/extra",
        "https://chatgpt.com:443/connector/oauth/test-callback",
        "https://user@chatgpt.com/connector/oauth/test-callback",
        "https://chatgpt.com/connector/oauth/test-callback?next=other",
        "https://chatgpt.com/connector/oauth/test-callback#fragment"
      })
  @DisplayName("공식 형식이 아닌 ChatGPT 콜백 설정은 거부한다")
  void invalidChatGptRedirectUriConfigurationIsRejected(String redirectUri) {
    assertThatThrownBy(
            () ->
                AgentAuthorizationServerConfig.AgentClients.validateChatGptRedirectUri(redirectUri))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("https://chatgpt.com/connector/oauth/{callback_id}");
  }

  @Test
  @DisplayName("등록되지 않은 리다이렉트는 여전히 거부한다")
  @WithAuthenticatedUser
  void unregisteredRedirectUriIsRejected() throws Exception {
    mockMvc
        .perform(authorize("https://evil.example.com/callback"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("공개 클라이언트도 인가 코드를 교환할 때 갱신 토큰을 받는다")
  @WithAuthenticatedUser
  void publicClientReceivesRefreshToken() throws Exception {
    grantConsent();

    mockMvc
        .perform(exchangeAuthorizationCode())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.refresh_token").exists());
  }

  @Test
  @DisplayName("code_verifier 가 어긋난 인가 코드 교환은 여전히 거부한다")
  @WithAuthenticatedUser
  void mismatchedCodeVerifierIsRejected() throws Exception {
    grantConsent();

    mockMvc
        .perform(exchangeAuthorizationCode("sync-agent-test-wrong-code-verifier-9876543210"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("invalid_grant"));
  }

  @Test
  @DisplayName("갱신 토큰을 회전해도 만료 시각은 최초 토큰의 것을 유지한다")
  @WithAuthenticatedUser
  void rotatedRefreshTokenKeepsOriginalExpiry() throws Exception {
    grantConsent();

    String refreshToken = refreshTokenOf(mockMvc.perform(exchangeAuthorizationCode()).andReturn());
    Instant originalExpiry = expiryOf(refreshToken);

    String rotated =
        refreshTokenOf(
            mockMvc
                .perform(
                    post("/oauth2/token")
                        .param("grant_type", "refresh_token")
                        .param("refresh_token", refreshToken)
                        .param("client_id", CLIENT_ID))
                .andExpect(status().isOk())
                .andReturn());

    assertThat(rotated).isNotEqualTo(refreshToken);
    assertThat(expiryOf(rotated)).isEqualTo(originalExpiry);
  }

  @Test
  @DisplayName("에이전트 글쓰기 빈이 컨텍스트에 올라온다")
  void agentBeansAreWired() {
    assertThat(agentPostService).isNotNull();
    assertThat(createPostTool).isNotNull();
  }

  @Test
  @DisplayName("본문이 빈 요청은 서비스 경계에서 막힌다 — MCP 도구에는 검증해 줄 컨트롤러가 없다")
  @WithAuthenticatedUser
  void blankBodyIsRejectedAtTheServiceBoundary() {
    CreateAgentPostRequest request =
        CreateAgentPostRequest.builder().type(PostType.SHORT).bodyMarkdown(" ").build();
    AuthorizedAgentClient client = new AuthorizedAgentClient("claude-code", "Claude Code");

    assertThatThrownBy(() -> agentPostService.createDraftPost(1L, request, client))
        .isInstanceOf(ConstraintViolationException.class);
  }

  /**
   * 인가 요청은 반드시 진짜 쿼리 문자열로 보내야 한다. Spring Authorization Server 는 파라미터를 {@code
   * request.getQueryString()} 에 그 이름이 들어 있는지로 걸러 내므로, MockMvc 의 {@code param()} 으로만 넣으면 파라미터가 통째로
   * 무시되고 {@code invalid_request} 가 돌아온다.
   */
  private void grantConsent() {
    grantConsent(CLIENT_ID);
  }

  private void grantConsent(String clientId) {
    RegisteredClient client = registeredClientRepository.findByClientId(clientId);

    authorizationConsentService.save(
        OAuth2AuthorizationConsent.withId(client.getId(), PRINCIPAL_NAME)
            .scope(AgentScopes.POSTS_DRAFT)
            .build());
  }

  private MockHttpServletRequestBuilder exchangeAuthorizationCode() throws Exception {
    return exchangeAuthorizationCode(CODE_VERIFIER);
  }

  private MockHttpServletRequestBuilder exchangeAuthorizationCode(String codeVerifier)
      throws Exception {
    return exchangeAuthorizationCode(CLIENT_ID, REDIRECT_URI, codeVerifier);
  }

  private MockHttpServletRequestBuilder exchangeAuthorizationCode(
      String clientId, String redirectUri, String codeVerifier) throws Exception {
    MvcResult authorized =
        mockMvc
            .perform(authorize(clientId, redirectUri))
            .andExpect(status().is3xxRedirection())
            .andReturn();
    String location = Objects.requireNonNull(authorized.getResponse().getRedirectedUrl());
    String code =
        UriComponentsBuilder.fromUriString(location).build().getQueryParams().getFirst("code");

    return post("/oauth2/token")
        .param("grant_type", "authorization_code")
        .param("code", Objects.requireNonNull(code))
        .param("redirect_uri", redirectUri)
        .param("client_id", clientId)
        .param("code_verifier", codeVerifier);
  }

  private static String refreshTokenOf(MvcResult result) throws Exception {
    return JsonPath.read(result.getResponse().getContentAsString(), "$.refresh_token");
  }

  private Instant expiryOf(String refreshToken) {
    OAuth2Authorization authorization =
        Objects.requireNonNull(
            authorizationService.findByToken(refreshToken, OAuth2TokenType.REFRESH_TOKEN));

    return Objects.requireNonNull(authorization.getRefreshToken()).getToken().getExpiresAt();
  }

  private static MockHttpServletRequestBuilder authorize(String redirectUri)
      throws NoSuchAlgorithmException {
    return authorize(CLIENT_ID, redirectUri);
  }

  private static MockHttpServletRequestBuilder authorize(String clientId, String redirectUri)
      throws NoSuchAlgorithmException {
    // 값을 퍼센트 인코딩하지 않는다. MockMvc 는 URL 의 쿼리를 디코딩하지 않고 그대로 파라미터로 넘기므로,
    // 인코딩해서 보내면 서버가 redirect_uri 를 인코딩된 문자열 그대로 받아 호스트조차 읽지 못한다.
    String query =
        "response_type=code"
            + "&client_id="
            + clientId
            + "&redirect_uri="
            + redirectUri
            + "&scope="
            + AgentScopes.POSTS_DRAFT
            + "&code_challenge="
            + codeChallenge()
            + "&code_challenge_method=S256";

    return get("/oauth2/authorize?" + query);
  }

  private static String codeChallenge() throws NoSuchAlgorithmException {
    byte[] digest =
        MessageDigest.getInstance("SHA-256").digest(CODE_VERIFIER.getBytes(StandardCharsets.UTF_8));

    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
  }
}
