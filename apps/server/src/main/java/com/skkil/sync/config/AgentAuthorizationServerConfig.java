package com.skkil.sync.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.auth.agent.AgentScopes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@ConditionalOnProperty(name = "app.agent.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class AgentAuthorizationServerConfig {

  private static final String LOCALHOST = "localhost";

  private final AgentOAuth2Properties properties;

  public AgentAuthorizationServerConfig(AgentOAuth2Properties properties) {
    this.properties = properties;
  }

  @Bean
  @Order(0)
  SecurityFilterChain agentAuthorizationServerFilterChain(
      HttpSecurity http,
      AgentOAuth2Properties properties,
      RegisteredClientRepository registeredClientRepository)
      throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServer =
        new OAuth2AuthorizationServerConfigurer();

    http.securityMatcher(authorizationServer.getEndpointsMatcher())
        .with(
            authorizationServer,
            server -> {
              server.authorizationServerMetadataEndpoint(
                  metadata ->
                      metadata.authorizationServerMetadataCustomizer(
                          builder ->
                              builder
                                  .scopes(scopes -> scopes.add(AgentScopes.POSTS_DRAFT))
                                  .tokenEndpointAuthenticationMethod(
                                      ClientAuthenticationMethod.NONE.getValue())));
              server.authorizationEndpoint(
                  endpoint -> endpoint.authenticationProviders(AgentRedirectUris::apply));
              server.clientAuthentication(
                  clientAuthentication -> {
                    clientAuthentication.authenticationConverter(
                        new PublicClientRefreshTokenAuthenticationConverter());
                    clientAuthentication.authenticationProviders(
                        providers ->
                            providers.add(
                                0,
                                new PublicClientRefreshTokenAuthenticationProvider(
                                    registeredClientRepository)));
                  });
            })
        .authorizeHttpRequests(requests -> requests.anyRequest().authenticated())
        .csrf(csrf -> csrf.ignoringRequestMatchers(authorizationServer.getEndpointsMatcher()))
        // 두 항목 모두 defaultAuthenticationEntryPointFor 로 등록해야 한다.
        // authenticationEntryPoint() 로 하나를 직접 지정하면 매핑 전체가 무시되고 브라우저까지
        // 401 을 받는다.
        .exceptionHandling(
            exception -> {
              exception.defaultAuthenticationEntryPointFor(
                  new ResumableLoginEntryPoint(properties.issuerUri() + "/auth/login"),
                  new MediaTypeRequestMatcher(MediaType.TEXT_HTML));
              exception.defaultAuthenticationEntryPointFor(
                  new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), AnyRequestMatcher.INSTANCE);
            });

    return http.build();
  }

  /**
   * 로그인 화면으로 보낼 때 원래 가려던 인가 요청을 {@code redirect} 파라미터로 함께 넘긴다. 로그인은 프론트엔드 페이지에서 이뤄지고 그 페이지는 세션 REST
   * 엔드포인트를 호출할 뿐이라, 서버의 요청 캐시가 저장해 둔 인가 요청은 아무도 다시 꺼내지 않는다. 파라미터로 넘기지 않으면 사용자는 로그인 뒤 홈으로 떨어지고 에이전트는
   * 콜백을 영영 받지 못한다.
   */
  static final class ResumableLoginEntryPoint extends LoginUrlAuthenticationEntryPoint {

    ResumableLoginEntryPoint(String loginFormUrl) {
      super(loginFormUrl);
    }

    @Override
    protected String determineUrlToUseForThisRequest(
        HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
      String target =
          request.getQueryString() == null
              ? request.getRequestURI()
              : request.getRequestURI() + "?" + request.getQueryString();

      return super.determineUrlToUseForThisRequest(request, response, e)
          + "?redirect="
          + URLEncoder.encode(target, StandardCharsets.UTF_8);
    }
  }

  @Bean
  AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().issuer(properties.issuerUri()).build();
  }

  @Bean
  RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
    return new JdbcRegisteredClientRepository(jdbcTemplate);
  }

  /**
   * 사전 등록 클라이언트를 채운다. 빈 생성 시점이 아니라 기동이 끝난 뒤에 하는 이유는, 빈 초기화 순서상 Liquibase 가 {@code
   * oauth2_registered_client} 테이블을 만들기 전에 저장을 시도할 수 있기 때문이다.
   */
  @Bean
  ApplicationRunner agentClientSeeder(RegisteredClientRepository registeredClientRepository) {
    return args ->
        AgentClients.seed(
            registeredClientRepository, properties.issuerUri(), properties.chatgptRedirectUri());
  }

  /**
   * 인가 기록 저장소. 인가 코드를 발급할 때 현재 인증된 주체({@link AuthenticatedUser})가 JSON 으로 직렬화되어 {@code
   * oauth2_authorization.attributes} 에 들어가고, 코드를 토큰으로 바꿀 때 다시 역직렬화된다.
   *
   * <p>기본 매퍼는 역직렬화 대상 타입을 허용 목록으로 제한한다(DB 내용이 오염됐을 때 임의 클래스가 살아나는 것을 막기 위한 장치다). 우리 주체 타입은 그 목록에
   * 없으므로 명시적으로 허용해 준다 — 검증기를 끄는 대신 이 한 타입만 더한다.
   */
  @Bean
  OAuth2AuthorizationService authorizationService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    JdbcOAuth2AuthorizationService service =
        new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);

    JsonMapper jsonMapper = agentAuthorizationJsonMapper();
    service.setAuthorizationRowMapper(
        new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(
            registeredClientRepository, jsonMapper));
    service.setAuthorizationParametersMapper(
        new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationParametersMapper(
            jsonMapper));

    return service;
  }

  private JsonMapper agentAuthorizationJsonMapper() {
    ClassLoader classLoader = AgentAuthorizationServerConfig.class.getClassLoader();
    BasicPolymorphicTypeValidator.Builder validator =
        BasicPolymorphicTypeValidator.builder().allowIfSubType(AuthenticatedUser.class);

    return JsonMapper.builder()
        .addModules(SecurityJacksonModules.getModules(classLoader, validator))
        .build();
  }

  @Bean
  OAuth2AuthorizationConsentService authorizationConsentService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
    return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
  }

  /**
   * 액세스 토큰에 {@code aud} 와 {@code client_id} 를 넣는다.
   *
   * <p>{@code aud} 는 리소스 서버가 검증하는 대상과 같아야 다른 곳에 발급된 토큰이 여기서 통하지 않는다.
   *
   * <p>{@code client_id} 는 기본으로 들어가지 않는다. 어느 에이전트가 쓴 글인지 기록하고 배지를 띄우려면 이 값이 필요하고, 사용량 제한 키도 여기서
   * 나오므로 직접 넣어 준다. 요청이 아니라 토큰에 서명되어 들어가므로 호출자가 다른 에이전트인 척할 수 없다.
   */
  @Bean
  OAuth2TokenCustomizer<JwtEncodingContext> agentTokenCustomizer() {
    return context ->
        context
            .getClaims()
            .audience(new ArrayList<>(List.of(properties.issuerUri())))
            .claim(JwtClaimNames.ISS, properties.issuerUri())
            .claim(OAuth2ParameterNames.CLIENT_ID, context.getRegisteredClient().getClientId());
  }

  @Bean
  OAuth2TokenGenerator<?> agentTokenGenerator(
      JWKSource<SecurityContext> jwkSource,
      OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
    JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource));
    jwtGenerator.setJwtCustomizer(jwtCustomizer);

    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, new OAuth2AccessTokenGenerator(), new AgentRefreshTokenGenerator());
  }

  static final class PublicClientRefreshTokenAuthenticationConverter
      implements AuthenticationConverter {

    @Override
    public @Nullable Authentication convert(HttpServletRequest request) {
      if (!AuthorizationGrantType.REFRESH_TOKEN
          .getValue()
          .equals(request.getParameter(OAuth2ParameterNames.GRANT_TYPE))) {
        return null;
      }

      String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
      if (clientId == null
          || clientId.isBlank()
          || request.getParameter(OAuth2ParameterNames.CLIENT_SECRET) != null) {
        return null;
      }

      return new OAuth2ClientAuthenticationToken(
          clientId,
          ClientAuthenticationMethod.NONE,
          null,
          Map.of(OAuth2ParameterNames.GRANT_TYPE, AuthorizationGrantType.REFRESH_TOKEN.getValue()));
    }
  }

  static final class PublicClientRefreshTokenAuthenticationProvider
      implements AuthenticationProvider {

    private final RegisteredClientRepository registeredClientRepository;

    PublicClientRefreshTokenAuthenticationProvider(
        RegisteredClientRepository registeredClientRepository) {
      this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) {
      OAuth2ClientAuthenticationToken clientAuthentication =
          (OAuth2ClientAuthenticationToken) authentication;

      if (!ClientAuthenticationMethod.NONE.equals(
              clientAuthentication.getClientAuthenticationMethod())
          || !AuthorizationGrantType.REFRESH_TOKEN
              .getValue()
              .equals(
                  clientAuthentication
                      .getAdditionalParameters()
                      .get(OAuth2ParameterNames.GRANT_TYPE))) {
        return null;
      }

      RegisteredClient registeredClient =
          registeredClientRepository.findByClientId(clientAuthentication.getPrincipal().toString());
      if (registeredClient == null
          || !registeredClient
              .getClientAuthenticationMethods()
              .contains(ClientAuthenticationMethod.NONE)
          || !registeredClient
              .getAuthorizationGrantTypes()
              .contains(AuthorizationGrantType.REFRESH_TOKEN)) {
        throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
      }

      return new OAuth2ClientAuthenticationToken(
          registeredClient, ClientAuthenticationMethod.NONE, null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
      return OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication);
    }
  }

  static final class AgentRefreshTokenGenerator
      implements OAuth2TokenGenerator<OAuth2RefreshToken> {

    private final StringKeyGenerator refreshTokenGenerator =
        new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

    @Override
    public @Nullable OAuth2RefreshToken generate(OAuth2TokenContext context) {
      if (!OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
        return null;
      }

      Instant issuedAt = Instant.now();
      Instant expiresAt =
          Optional.ofNullable(context.getAuthorization())
              .map(OAuth2Authorization::getRefreshToken)
              .map(token -> token.getToken().getExpiresAt())
              .orElseGet(
                  () ->
                      issuedAt.plus(
                          context
                              .getRegisteredClient()
                              .getTokenSettings()
                              .getRefreshTokenTimeToLive()));

      return new OAuth2RefreshToken(refreshTokenGenerator.generateKey(), issuedAt, expiresAt);
    }
  }

  @Bean
  JWKSource<SecurityContext> jwkSource() {
    RSAKey key = AgentSigningKeys.resolve(properties);

    return new ImmutableJWKSet<>(new JWKSet(key));
  }

  /**
   * {@code http://localhost:<임의 포트>/callback} 형태의 리다이렉트를 받아 준다.
   *
   * <p>Spring Authorization Server 는 RFC 8252 §7.3 을 따라 포트를 무시하고 비교하지만, 그 대상은 {@code 127.0.0.0/8} 과
   * {@code [::1]} 같은 IP 리터럴뿐이다. 문자열 {@code localhost} 는 포함되지 않으므로 등록된 {@code
   * http://localhost/callback} 은 포트가 붙는 순간 어긋난다 — MCP 클라이언트는 매번 빈 포트를 잡아 콜백 리스너를 열기 때문에 사실상 항상
   * 어긋난다.
   *
   * <p>스킴·경로·쿼리가 모두 같고 호스트가 {@code localhost} 인 등록 URI 가 있을 때만 통과시키고, 나머지는 기본 검증기로 넘긴다.
   */
  static final class AgentRedirectUris {

    private AgentRedirectUris() {}

    static void apply(List<AuthenticationProvider> providers) {
      Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> redirectUriValidator =
          AgentRedirectUris::validateRedirectUri;
      Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> validator =
          redirectUriValidator.andThen(
              OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_SCOPE_VALIDATOR);

      for (AuthenticationProvider provider : providers) {
        if (provider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider codeProvider) {
          codeProvider.setAuthenticationValidator(validator);
        }
      }
    }

    private static void validateRedirectUri(
        OAuth2AuthorizationCodeRequestAuthenticationContext context) {
      OAuth2AuthorizationCodeRequestAuthenticationToken authentication =
          context.getAuthentication();
      if (matchesRegisteredLocalhostUri(
          context.getRegisteredClient(), authentication.getRedirectUri())) {
        return;
      }

      OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_REDIRECT_URI_VALIDATOR.accept(
          context);
    }

    private static boolean matchesRegisteredLocalhostUri(
        RegisteredClient client, @Nullable String requestedRedirectUri) {
      if (requestedRedirectUri == null) {
        return false;
      }

      UriComponents requested = parse(requestedRedirectUri);
      if (requested == null
          || requested.getFragment() != null
          || !LOCALHOST.equals(requested.getHost())) {
        return false;
      }

      return client.getRedirectUris().stream()
          .map(AgentRedirectUris::parse)
          .anyMatch(
              registered ->
                  registered != null
                      && LOCALHOST.equals(registered.getHost())
                      && Objects.equals(registered.getScheme(), requested.getScheme())
                      && Objects.equals(registered.getPath(), requested.getPath())
                      && Objects.equals(registered.getQuery(), requested.getQuery()));
    }

    private static @Nullable UriComponents parse(String uri) {
      try {
        return UriComponentsBuilder.fromUriString(uri).build();
      } catch (RuntimeException e) {
        return null;
      }
    }
  }

  /** 사전 등록된 에이전트 클라이언트. Phase 1 에서는 동적 클라이언트 등록(RFC 7591)을 쓰지 않는다. */
  static final class AgentClients {

    private static final String CHATGPT_REDIRECT_PATH_PREFIX = "/connector/oauth/";

    private AgentClients() {}

    private static final List<String> VENDORS =
        List.of("claude-code", "cursor", "codex", "windsurf", "generic-mcp");

    static void seed(
        RegisteredClientRepository repository,
        String issuerUri,
        @Nullable String chatgptRedirectUri) {
      for (String vendor : VENDORS) {
        // 로컬 MCP 클라이언트는 실행할 때마다 루프백 포트가 바뀔 수 있다. Spring Authorization
        // Server 는 IP 리터럴의 포트를 무시하고, localhost 형태는 AgentRedirectUris 가 허용한다.
        seedClient(
            repository,
            vendor,
            List.of(
                "http://127.0.0.1/callback",
                "http://localhost/callback",
                issuerUri + "/oauth2/callback"));
      }

      if (chatgptRedirectUri != null && !chatgptRedirectUri.isBlank()) {
        validateChatGptRedirectUri(chatgptRedirectUri);
        seedClient(repository, "chatgpt", List.of(chatgptRedirectUri));
      }
    }

    static void validateChatGptRedirectUri(String redirectUri) {
      UriComponents uri;
      try {
        uri = UriComponentsBuilder.fromUriString(redirectUri).build();
      } catch (RuntimeException e) {
        throw invalidChatGptRedirectUri(e);
      }

      String path = uri.getPath();
      boolean hasCallbackId =
          path != null
              && path.startsWith(CHATGPT_REDIRECT_PATH_PREFIX)
              && path.length() > CHATGPT_REDIRECT_PATH_PREFIX.length()
              && path.indexOf('/', CHATGPT_REDIRECT_PATH_PREFIX.length()) < 0;

      if (!"https".equalsIgnoreCase(uri.getScheme())
          || !"chatgpt.com".equalsIgnoreCase(uri.getHost())
          || uri.getPort() != -1
          || uri.getUserInfo() != null
          || uri.getQuery() != null
          || uri.getFragment() != null
          || !hasCallbackId) {
        throw invalidChatGptRedirectUri(null);
      }
    }

    private static IllegalStateException invalidChatGptRedirectUri(
        @Nullable RuntimeException cause) {
      return new IllegalStateException(
          "app.agent.chatgpt-redirect-uri 는 "
              + "https://chatgpt.com/connector/oauth/{callback_id} 형식이어야 합니다.",
          cause);
    }

    private static void seedClient(
        RegisteredClientRepository repository, String vendor, List<String> redirectUris) {
      RegisteredClient existing = repository.findByClientId(vendor);

      repository.save(
          build(
              vendor,
              redirectUris,
              existing == null ? UUID.randomUUID().toString() : existing.getId()));

      if (existing == null) {
        log.info("에이전트 OAuth2 클라이언트를 등록했습니다: {}", vendor);
      }
    }

    private static RegisteredClient build(String vendor, List<String> redirectUris, String id) {
      RegisteredClient.Builder builder =
          RegisteredClient.withId(id)
              .clientId(vendor)
              .clientName(displayName(vendor))
              // 에이전트 클라이언트는 비밀값을 안전하게 보관할 수 없는 공개 클라이언트다. 시크릿 없이
              // PKCE 로만 보호한다.
              .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
              .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
              .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
              .scope(AgentScopes.POSTS_DRAFT)
              .clientSettings(
                  ClientSettings.builder()
                      .requireProofKey(true)
                      .requireAuthorizationConsent(true)
                      .build())
              .tokenSettings(
                  TokenSettings.builder()
                      .accessTokenTimeToLive(Duration.ofHours(24))
                      .refreshTokenTimeToLive(Duration.ofDays(30))
                      .reuseRefreshTokens(false)
                      .build());

      redirectUris.forEach(builder::redirectUri);

      return builder.build();
    }

    private static String displayName(String vendor) {
      return switch (vendor) {
        case "claude-code" -> "Claude Code";
        case "cursor" -> "Cursor";
        case "codex" -> "Codex";
        case "windsurf" -> "Windsurf";
        case "chatgpt" -> "ChatGPT";
        default -> "MCP Client";
      };
    }
  }

  /**
   * 서명 키 해석. 루프백 발급자(로컬 개발)에서만 임시 키를 만들고, 그 외에는 키가 없으면 기동을 멈춘다 — 임시 키로 뜨면 재시작마다 발급된 토큰이 전부 무효가 되고
   * 인스턴스가 둘 이상이면 서로 토큰을 검증하지 못하는데, 그 사실이 로그 한 줄로만 남으면 아무도 알아채지 못한다.
   */
  static final class AgentSigningKeys {

    private AgentSigningKeys() {}

    static RSAKey resolve(AgentOAuth2Properties properties) {
      String privateKey = properties.rsaPrivateKey();
      String publicKey = properties.rsaPublicKey();
      // 설정하지 않은 프로퍼티는 null 이 아니라 빈 문자열로 들어온다(application.yaml 의 기본값).
      if (privateKey != null
          && !privateKey.isBlank()
          && publicKey != null
          && !publicKey.isBlank()) {
        return parsePem(publicKey, privateKey);
      }

      if (!isLoopbackIssuer(properties.issuerUri())) {
        throw new IllegalStateException(
            "app.agent.rsa-private-key / app.agent.rsa-public-key 가 설정되지 않았습니다. "
                + "임시 서명 키로 뜨면 재시작마다 기존 액세스 토큰이 모두 무효가 되므로 "
                + "루프백이 아닌 발급자("
                + properties.issuerUri()
                + ")에서는 키를 반드시 설정해야 합니다.");
      }

      log.warn("app.agent 의 RSA 키가 없어 임시 서명 키를 생성합니다. 재시작하면 기존 액세스 토큰이 모두 무효가 됩니다.");

      KeyPair keyPair = generate();

      return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
          .privateKey((RSAPrivateKey) keyPair.getPrivate())
          .keyID(UUID.randomUUID().toString())
          .build();
    }

    private static boolean isLoopbackIssuer(String issuerUri) {
      try {
        String host = UriComponentsBuilder.fromUriString(issuerUri).build().getHost();

        return host != null
            && (host.equals(LOCALHOST) || host.startsWith("127.") || host.equals("[::1]"));
      } catch (RuntimeException e) {
        return false;
      }
    }

    private static KeyPair generate() {
      try {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        return generator.generateKeyPair();
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("RSA 키를 생성할 수 없습니다", e);
      }
    }

    /** PEM 본문(헤더/개행 유무와 무관)을 읽어 서명용 키 쌍을 만든다. 개인키는 PKCS#8, 공개키는 X.509 형식이다. */
    private static RSAKey parsePem(String publicKeyPem, String privateKeyPem) {
      try {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey =
            (RSAPublicKey)
                keyFactory.generatePublic(new X509EncodedKeySpec(decodePem(publicKeyPem)));
        RSAPrivateKey privateKey =
            (RSAPrivateKey)
                keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodePem(privateKeyPem)));

        return new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.nameUUIDFromBytes(publicKey.getEncoded()).toString())
            .build();
      } catch (GeneralSecurityException e) {
        throw new IllegalStateException("app.agent 의 RSA 키를 읽을 수 없습니다", e);
      }
    }

    private static byte[] decodePem(String pem) {
      String body = pem.replaceAll("-----(BEGIN|END)[^-]*-----", "").replaceAll("\\s", "");

      return Base64.getDecoder().decode(body);
    }
  }
}
