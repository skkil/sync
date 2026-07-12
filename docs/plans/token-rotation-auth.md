# Plan: 장기 세션을 위한 단기 액세스 토큰 + 장기 리프레시 토큰(로테이션) 인증

## Goal

현재 apps/server는 Spring Session JDBC 기반의 세션 쿠키 인증만
사용하며(`SPRING_SESSION` 테이블, 요청마다 DB 조회), apps/web의 Better Auth는 이
세션 쿠키를 검증만 하는 클라이언트 측 미러일 뿐 독립적인 토큰 체계가 아니다.
토큰 관련 코드는 전혀 존재하지 않는다(그린필드 설계).

이번 변경의 목표는 다음을 만족하는 인증 체계로 교체하는 것이다.

- **단기 액세스 토큰**(opaque, DB 검증, 예: 15분)으로 매 요청 인증
- **장기 리프레시 토큰**(opaque, 회전/rotation)으로 액세스 토큰을 재발급
- 리프레시마다 이전 토큰은 즉시 무효화되고 새 토큰이 발급됨(rotation)
- 이미 회전되어 무효화된 리프레시 토큰이 재사용되면 **탈취로 간주**하고 해당
  로그인 세션 전체를 폐기(reuse detection)
- 서버(Spring Security)와 클라이언트(Next.js/Better Auth) 양쪽 모두 이 흐름을
  지원

## 확정된 설계 축 (사용자 확인 완료)

- **토큰 전달 방식**: httpOnly 쿠키 (access_token, refresh_token 둘 다). 기존
  SSR 쿠키 포워딩 구조(`apps/web/src/lib/server.ts`, `util/server.ts`)와 CSRF
  더블서브밋 패턴을 그대로 재사용한다.
- **액세스 토큰 형식**: Opaque 토큰 + DB 검증 (JWT 아님). 매 요청 DB 조회는
  유지되지만, 즉시 폐기 가능(revocation)하고 세션/리프레시 개념과 도메인 모델을
  명확히 분리할 수 있다.

## System context

- 현재 인증은 `SecurityConfig`(폼 로그인 비활성화, DaoAuthenticationProvider) +
  `OAuth2SecurityConfig`(Google OIDC, 별도 필터체인) +
  `SessionConfig`(`@EnableJdbcHttpSession`)로 구성되어 있다. 로그인 시 세션
  픽세이션 방지를 위해 `http.changeSessionId()`를 호출하는 코드가
  `AuthController`에 막 추가됨(커밋 `b0a2c71b`, 오늘).
- CSRF는 `CookieCsrfTokenRepository.withHttpOnlyFalse()`로 세션과 무관하게 쿠키
  자체에 토큰을 저장하는 방식이라 세션 저장소를 걷어내도 영향받지 않는다.
- apps/web은 서버 컴포넌트(RSC)가 많아 클라이언트 메모리에 토큰을 들고 있을 수
  없다 — 이것이 bearer-header 방식을 배제하고 쿠키 방식을 선택한 핵심 이유다.
- OAuth2 로그인은 Spring Security 기본
  `HttpSessionOAuth2AuthorizationRequestRepository`를 사용 중이라, 세션을
  STATELESS로 바꾸면 OAuth2 핸드셰이크 자체가 깨진다 — 이 부분은 별도의 쿠키
  기반 authorization-request 저장소가 필요하다(아래 변경 지점 참고).
- WebSocket(`/ws`, STOMP)은 별도의 인증 로직이 없고 HTTP 핸드셰이크 시점에
  Spring Security 필터체인을 그대로 통과하므로, 액세스 토큰 쿠키 인증 필터만
  정상 동작하면 추가 변경 없이 동작할 것으로 보인다 (빌드 단계에서 검증 필요,
  아래 명시).

## Data structures

### `refresh_tokens` (신규 테이블)

| 컬럼                  | 타입                               | 설명                                                                                                                                                   |
| --------------------- | ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `id`                  | UUID PK                            |                                                                                                                                                        |
| `user_id`             | BIGINT FK → `users`                |                                                                                                                                                        |
| `family_id`           | UUID                               | 하나의 로그인(최초 발급)에서 파생된 모든 회전 토큰이 공유하는 lineage ID. Reuse 감지 시 이 ID로 전체 폐기                                              |
| `token_hash`          | VARCHAR(64) UNIQUE                 | 토큰 원문은 저장하지 않고 SHA-256 해시만 저장 (`email_verification_tokens` 패턴과 달리, 리프레시 토큰은 탈취 시 장기 세션 전체가 위험하므로 해시 저장) |
| `issued_at`           | TIMESTAMPTZ                        |                                                                                                                                                        |
| `expires_at`          | TIMESTAMPTZ                        | 절대 만료 시각 (예: 발급 후 30일)                                                                                                                      |
| `absolute_expires_at` | TIMESTAMPTZ                        | **family 최초 발급 시각 기준** 절대 상한(예: 90일). 활동이 있어도 이 시점 이후엔 재로그인 강제                                                         |
| `rotated_at`          | TIMESTAMPTZ NULL                   | 이 토큰이 새 토큰으로 교환된 시각. NULL이면 아직 미사용                                                                                                |
| `revoked_at`          | TIMESTAMPTZ NULL                   | 명시적 폐기 시각(로그아웃, reuse 감지, 관리자 강제 로그아웃 등)                                                                                        |
| `replaced_by_id`      | UUID NULL FK → `refresh_tokens.id` | 회전으로 이 토큰을 대체한 다음 토큰                                                                                                                    |
| `user_agent`          | TEXT NULL                          | 감사/기기 목록용 (선택)                                                                                                                                |
| `ip_address`          | INET NULL                          | 감사용 (선택)                                                                                                                                          |
| `created_at`          | TIMESTAMPTZ                        |                                                                                                                                                        |

**설계 근거 (illegal state 배제)**: 사용 완료된 토큰을 삭제하지 않고
`rotated_at`/`revoked_at` 타임스탬프 플래그로 표시한다. 삭제하면 reuse(재전송
공격) 자체를 감지할 방법이 없어지기 때문이다 — architect 스킬 예시의 "죽었지만
애니메이션을 위해 갱신이 필요한 엔티티는 배열에서 제거하지 않고 dead 플래그로
표시한다"는 원칙과 동일하다. "사용 가능한 리프레시 토큰"은 항상
`rotated_at IS NULL AND revoked_at IS NULL AND expires_at > now() AND absolute_expires_at > now()`
로만 표현되며, 이 조건을 만족하지 않는 토큰은 코드 경로상 재사용이 불가능하다.

### `access_tokens` (신규 테이블)

| 컬럼               | 타입                          | 설명                                                                         |
| ------------------ | ----------------------------- | ---------------------------------------------------------------------------- |
| `id`               | UUID PK                       |                                                                              |
| `user_id`          | BIGINT FK → `users`           | 조회 시 조인 없이 바로 사용                                                  |
| `refresh_token_id` | UUID FK → `refresh_tokens.id` | 이 액세스 토큰이 속한 리프레시 체인(family). family 전체 폐기 시 연쇄 폐기용 |
| `token_hash`       | VARCHAR(64) UNIQUE            |                                                                              |
| `expires_at`       | TIMESTAMPTZ                   | 짧은 TTL (예: 15분)                                                          |
| `revoked_at`       | TIMESTAMPTZ NULL              | 로그아웃/family 폐기 시 함께 무효화                                          |
| `created_at`       | TIMESTAMPTZ                   |                                                                              |

**Invariant**: `access_tokens.refresh_token_id`가 가리키는 리프레시 토큰이
폐기되면, 해당 액세스 토큰도 (지연 없이) 유효하지 않은 것으로 간주되어야 한다 —
FK로 강제할 수 없으므로 검증 로직에서 "부모 리프레시 토큰이 revoked_at이
아닌지"를 함께 확인하거나, family 폐기 시 자식 액세스 토큰들의 `revoked_at`도
함께 UPDATE한다. 후자를 택해 검증 쿼리를 단순하게 유지한다.

### `SPRING_SESSION` / `SPRING_SESSION_ATTRIBUTES`

인증 목적으로는 더 이상 사용하지 않는다.
`SessionConfig`(`@EnableJdbcHttpSession`)를 제거하고
`SessionCreationPolicy.STATELESS`로 전환한다. 기존 테이블은 별도
마이그레이션으로 drop할지, 당분간 남겨둘지는 Open Decision으로 남긴다(아래
참고).

## Interfaces

### 서버: `TokenService` (신규, `com.skkil.sync.auth.token` 패키지 제안)

```java
public interface TokenService {
  TokenPair issueTokenPair(AuthenticatedUser user, HttpServletRequest request);
  TokenPair rotateRefreshToken(String presentedRefreshToken, HttpServletRequest request);
  void revokeSession(String refreshToken); // 로그아웃: 해당 family만 폐기
  void revokeAllSessionsForUser(Long userId); // 비밀번호 변경 등 보안 이벤트
  AuthenticatedUser validateAccessToken(String accessToken);
}

public record TokenPair(String accessToken, Instant accessTokenExpiresAt,
                         String refreshToken, Instant refreshTokenExpiresAt) {}
```

`rotateRefreshToken`의 reuse 감지 로직:

1. 제시된 토큰 해시로 `refresh_tokens` 조회.
2. 없으면 401.
3. `revoked_at`이 이미 설정돼 있으면 → **reuse 의심**, 해당 `family_id`
   전체(리프레시+액세스) 폐기 후 401 (사용자는 반드시 재로그인해야 함).
4. `rotated_at`이 이미 설정돼 있고 재사용 시각이 grace window(아래 참고, 예:
   10초)를 벗어났다면 → 3과 동일하게 reuse로 간주.
5. `rotated_at`이 이미 설정돼 있지만 grace window 이내라면 → 이미 발급된
   `replaced_by_id` 토큰 쌍을 **그대로 재반환**(멱등 처리, 새로 발급하지 않음).
   동시 요청 경쟁 방지용.
6. 정상 케이스: 현재 토큰에 `rotated_at` 설정 + 같은 `family_id`로 새
   리프레시/액세스 토큰 발급, `replaced_by_id` 연결.

### 서버: 인증 필터 `AccessTokenAuthenticationFilter` (신규, `OncePerRequestFilter`)

`access_token` 쿠키를 읽어 `TokenService.validateAccessToken()`으로 검증 후
`SecurityContext`에 `Authentication`을 설정. 기존
`HttpSessionSecurityContextRepository` 기반 흐름을 대체.

### 서버: 쿠키 기반 OAuth2 Authorization Request Repository (신규)

`AuthorizationRequestRepository<OAuth2AuthorizationRequest>`를 구현해 OAuth2
핸드셰이크 동안만 쓰이는 상태를 짧은 TTL의 서명된 쿠키에 직렬화 저장(Spring
Security가 세션 없이 OAuth2 로그인을 지원할 때 쓰는 표준 패턴).
`OAuth2SecurityConfig`에 등록.

### 서버: 신규/변경 엔드포인트

- `POST /auth/login`, `POST /auth/register` — 세션 생성 대신
  `tokenService.issueTokenPair()` 호출, `access_token`/`refresh_token` 쿠키
  설정.
- `POST /auth/refresh` (신규) — `refresh_token` 쿠키를 읽어
  `rotateRefreshToken()` 호출, 새 쿠키 설정. Bucket4j rate limit 대상에
  추가(리플레이 무차별 공격 방지).
- `POST /auth/logout` — `refresh_token` 쿠키로 `revokeSession()` 호출 후 두 쿠키
  모두 만료 처리.
- `OAuth2AuthenticationSuccessHandler` — 세션 대신
  `tokenService.issueTokenPair()` 호출 후 리다이렉트.

### 쿠키 스코프

- `access_token`: `Path=/`, `HttpOnly`, `Secure`, `SameSite=Lax`, TTL ≈ 15분.
- `refresh_token`: `Path=/auth`, `HttpOnly`, `Secure`, `SameSite=Lax`, TTL ≈
  30일(절대 상한 90일). Path를 `/auth`로 제한해 `/auth/refresh`, `/auth/logout`
  등에서만 브라우저가 자동 전송하도록 하여 노출 표면을 줄인다.

### 웹: `apps/web/src/lib/server.ts`

- `beforeError` 401 처리 로직 변경: 즉시 로그아웃하지 않고, 먼저
  `POST /auth/refresh` 시도 → 성공 시 원 요청 재시도(1회) → 실패 시 기존처럼
  Better Auth 세션 무효화 + 로그인 페이지 리다이렉트.
- 동시에 여러 요청이 401을 받는 경쟁 상태를 막기 위해 **single-flight
  프라미스**로 리프레시 호출을 직렬화(모듈 스코프 변수 하나로 진행 중인 리프레시
  프라미스를 공유).
- 서버 측 grace window(위 TokenService 5번)는 SSR 요청과 브라우저 요청처럼
  클라이언트 single-flight로 묶을 수 없는 별개 프로세스 간 경쟁까지 방어하기
  위한 보완 장치.

### 웹: `apps/web/src/lib/auth/index.ts` (Better Auth `spring-session` 플러그인)

- `ctx.getCookie('session')` → `ctx.getCookie('access_token')`로 대상 쿠키 변경.
- 검증 호출(`getAuthenticatedUser()`)이 401을 받으면 곧바로 로그아웃 처리하지
  말고, `/auth/refresh`를 한 번 시도한 뒤 재검증하도록 훅 로직에 리프레시 단계를
  추가(그렇지 않으면 액세스 토큰이 15분마다 만료될 때 Better Auth 미러가 조기에
  로그아웃 처리해버림 — 리프레시 토큰이 살아있는데도).

## Change sites

Server:

- `apps/server/src/main/resources/db/changelog/changesets/` — 신규 changeset:
  `refresh_tokens`, `access_tokens` 테이블 생성 (+ `db.changelog-master.yaml`에
  등록).
- `apps/server/src/main/java/com/skkil/sync/auth/token/` (신규 패키지) —
  `TokenService`, `TokenServiceImpl`, `TokenPair`,
  `AccessTokenAuthenticationFilter`.
- `apps/server/src/main/java/com/skkil/sync/config/SecurityConfig.java` —
  `SessionCreationPolicy.STATELESS`로 변경, `AccessTokenAuthenticationFilter`를
  필터체인에 등록, `http.changeSessionId()` 로직 제거(세션 픽세이션 방지는 더
  이상 세션이 아니라 매 로그인마다 새 `family_id`를 발급하는 것으로 대체됨 —
  사실상 상위 호환).
- `apps/server/src/main/java/com/skkil/sync/config/SessionConfig.java` — 삭제
  (`@EnableJdbcHttpSession` 제거).
- `apps/server/src/main/java/com/skkil/sync/config/OAuth2SecurityConfig.java` —
  쿠키 기반 `AuthorizationRequestRepository` 등록, STATELESS 정책 적용.
- `apps/server/src/main/java/com/skkil/sync/user/controller/AuthController.java`
  — 로그인/로그아웃 로직을 `TokenService` 기반으로 교체, `POST /auth/refresh`
  신규 엔드포인트 추가.
- `apps/server/src/main/java/com/skkil/sync/auth/handler/OAuth2AuthenticationSuccessHandler.java`
  — `TokenService.issueTokenPair()` 호출 추가.
- `apps/server/src/main/java/com/skkil/sync/config/BucketConfig.java` / rate
  limit 필터 URL 패턴 — `/auth/refresh` 추가.
- `apps/server/src/main/java/com/skkil/sync/config/CorsConfig.java` — 변경
  불필요 확인만(쿠키 기반이라 `allowCredentials=true` 유지 조건 동일).
- 신규 `@Scheduled` 정리 작업 — 만료/폐기된 `access_tokens`/`refresh_tokens`
  주기적 삭제 (Bucket4j의 hourly cleanup과 동일 패턴).
- `apps/server/src/main/java/com/skkil/sync/config/WebSocketConfig.java` — 코드
  변경은 불필요할 것으로 예상되나, `/ws` 핸드셰이크가 새
  `AccessTokenAuthenticationFilter`를 정상적으로 통과하는지 빌드 단계에서 실제
  검증 필요.

Web:

- `apps/web/src/lib/server.ts` — 401 처리에 리프레시-후-재시도 + single-flight
  로직 추가.
- `apps/web/src/lib/auth/index.ts` — 쿠키 이름 변경(`session` → `access_token`),
  검증 실패 시 리프레시 선시도 로직 추가.
- `apps/web/src/lib/auth/session.ts` — 캐싱 정책 재검토(액세스 토큰 TTL이
  15분이므로 기존 "세션 재사용" 가정이 바뀜 — 상세는 빌드 단계에서 확인).
- `apps/web/src/util/server.ts` — 쿠키 이름/개수 변경(access_token +
  refresh_token 두 개 포워딩) 반영.
- 로그아웃 관련 클라이언트 코드(`useSession`/`signOut` 경로) — 서버의
  `/auth/logout`이 이제 refresh family를 명시적으로 폐기하므로 동작은 동일하나
  응답 코드/쿠키 삭제 여부 확인.

## Invariants

1. **단일 사용 리프레시 토큰**: 유효한(미회전, 미폐기, 미만료) 리프레시 토큰은
   항상 정확히 하나의 미래 토큰으로만 교환될 수 있다. 교환 즉시 `rotated_at`이
   설정되어 재사용 불가 상태가 된다.
2. **Reuse는 항상 family 전체 폐기로 이어진다**: 이미 회전/폐기된 토큰이 grace
   window를 벗어나 재제시되면 해당 `family_id`의 모든 리프레시/액세스 토큰이
   즉시 폐기되고, 사용자는 반드시 재로그인해야 한다. (탈취된 리프레시 토큰이
   공격자와 정상 사용자 양쪽에서 쓰이는 경쟁을 조기 차단)
3. **절대 만료 상한**: `absolute_expires_at`은 family 생성 이후 활동 여부와
   무관하게 고정되며, 아무리 자주 갱신해도 이 시점을 넘길 수 없다. "장기 세션"이
   무한 세션이 되지 않도록 보장.
4. **액세스 토큰은 부모가 죽으면 함께 죽는다**: family가 폐기되면 그 family에서
   발급된 모든 액세스 토큰도 (남은 TTL과 무관하게) 즉시 무효화된다.
5. **grace window는 rotation 한정**: reuse 감지의 grace window(정상적인 동시
   요청 경쟁 흡수용)는 "직전 1회 회전"에만 적용되고, 그보다 오래된 토큰이나
   명시적으로 폐기(`revoked_at`)된 토큰에는 적용되지 않는다 — 그렇지 않으면 진짜
   탈취를 grace로 놓칠 수 있다.
6. **쿠키 스코프 최소화**: `refresh_token` 쿠키는 `/auth` 경로 이외의 요청에는
   전송되지 않는다 (일반 API 호출에 리프레시 토큰이 노출되지 않도록).
7. **CSRF 보호 유지**: 토큰이 쿠키에 저장되므로 CSRF 위협은 오늘과 동일하게
   존재하며, 기존 `CookieCsrfTokenRepository` 더블서브밋 패턴을 모든 상태 변경
   엔드포인트(`/auth/refresh` 포함)에 계속 적용한다.

## Open decisions

1. **`SPRING_SESSION` 테이블 처리**: 인증 용도로는 즉시 미사용 상태가 되는데,
   (a) 같은 changeset에서 바로 drop할지, (b) 혹시 다른 용도(예: 향후 재도입,
   디버깅)로 당분간 남겨둘지. 다른 기능이 세션에 의존하는 코드가 없음을
   재확인했으므로 drop을 권장하지만, 운영 DB에 이미 쌓인 세션 데이터가 있다면
   배포 순서를 고려해야 한다.
2. **Grace window 길이**: 10초를 제안했으나, 실제 클라이언트 동시성 패턴(SSR +
   브라우저가 동시에 401을 맞는 빈도)에 따라 조정 필요. 너무 길면 진짜 탈취
   탐지가 늦어지고, 너무 짧으면 정상 경쟁 상황에서도 오탐(정상 사용자 강제
   로그아웃)이 발생한다.
3. **"다른 기기 로그아웃" / 세션 목록 기능**: `family_id` +
   `user_agent`/`ip_address` 컬럼을 이미 설계에 포함했지만, 이번 스코프에
   "로그인된 기기 목록 보기/개별 로그아웃 UI"를 포함할지는 별도 확인이 필요하다.
   포함하지 않는다면 해당 컬럼들은 당장은 감사 로그 용도로만 채워둔다.
4. **비밀번호 변경/보안 이벤트 시 전체 세션 폐기 연동 범위**:
   `revokeAllSessionsForUser()` 인터페이스는 설계에 포함했으나, 실제로 이번
   변경에서 비밀번호 변경 플로우에 연결할지, 후속 작업으로 미룰지 결정 필요.
5. **액세스/리프레시 토큰 TTL 구체값**: 15분/30일/90일을 기본값으로 제안했으나
   최종 값은 보안 요구사항에 맞춰 조정 가능.
