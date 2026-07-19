import { betterAuth } from 'better-auth';
import { createAuthMiddleware } from 'better-auth/api';
import { setSessionCookie } from 'better-auth/cookies';
import { nextCookies } from 'better-auth/next-js';
import { HTTPError } from 'ky';

import { getUserPreferences } from '@/api/__generated__/preferences/preferences';
import { getAuthenticatedUser } from '@/api/__generated__/profile/profile';

// spring session 쿠키는 http-only이므로 원본 값을 그대로 저장/노출하면 안 되고,
// 동일 로그인 여부만 비교할 수 있으면 충분하므로 해시로 변환해 저장한다.
async function hashSpringSessionCookie(value: string) {
  const digest = await crypto.subtle.digest(
    'SHA-256',
    new TextEncoder().encode(value),
  );
  return Array.from(new Uint8Array(digest))
    .map((byte) => byte.toString(16).padStart(2, '0'))
    .join('');
}

export const auth = betterAuth({
  user: {
    additionalFields: {
      handle: {
        type: 'string',
        input: false,
      },
      isOnboarded: {
        type: 'boolean',
        input: false,
      },
      role: {
        type: 'string',
        input: false,
        defaultValue: 'USER',
      },
      theme: {
        type: 'string',
        input: false,
        defaultValue: 'system',
      },
    },
  },
  session: {
    additionalFields: {
      springSessionHash: {
        type: 'string',
        input: false,
      },
    },
  },
  plugins: [
    nextCookies(),
    {
      id: 'spring-session',
      hooks: {
        after: [
          {
            matcher: () => true,
            handler: createAuthMiddleware(async (ctx) => {
              const sessionCookie = ctx.getCookie('session');
              if (!sessionCookie) {
                return null;
              }
              const sessionCookieHash =
                await hashSpringSessionCookie(sessionCookie);
              const forceSync =
                String(ctx.query?.disableCookieCache) === 'true';

              const existingToken = await ctx.getSignedCookie(
                ctx.context.authCookies.sessionToken.name,
                ctx.context.secret,
              );

              if (existingToken) {
                const existingSession =
                  await ctx.context.internalAdapter.findSession(existingToken);

                if (
                  !forceSync &&
                  existingSession &&
                  new Date(existingSession.session.expiresAt) > new Date() &&
                  existingSession.session.springSessionHash ===
                    sessionCookieHash
                ) {
                  return null;
                }
              }

              let response;
              try {
                response = (await getAuthenticatedUser()).data;
              } catch (error) {
                // 백엔드가 명시적으로 401을 반환했을 때만 실제 미인증으로 간주하고
                // 세션 쿠키를 무효화한다. 네트워크 오류, 타임아웃, 5xx 등 일시적인
                // 장애까지 로그아웃으로 취급하면 정상 세션도 강제로 끊기게 된다.
                if (
                  error instanceof HTTPError &&
                  error.response.status === 401
                ) {
                  await ctx.setSignedCookie(
                    ctx.context.authCookies.sessionToken.name,
                    '',
                    ctx.context.secret,
                    {
                      maxAge: -1,
                    },
                  );
                }

                return null;
              }

              const {
                userId,
                name,
                handle,
                email,
                profileImageUrl,
                isOnboarded,
                role,
              } = response;

              const {
                data: { theme },
              } = await getUserPreferences().catch(() => ({
                data: { theme: 'system' },
              }));

              const existingUser =
                await ctx.context.internalAdapter.findUserById(String(userId));

              const user = existingUser
                ? await ctx.context.internalAdapter.updateUser(
                    existingUser.id,
                    {
                      name,
                      handle,
                      image: profileImageUrl,
                      isOnboarded,
                      role,
                      theme,
                    },
                  )
                : await ctx.context.internalAdapter.createUser({
                    id: userId,
                    name,
                    handle,
                    email,
                    image: profileImageUrl,
                    isOnboarded,
                    role,
                    theme,
                  });

              const session = await ctx.context.internalAdapter.createSession(
                userId,
                true,
                { springSessionHash: sessionCookieHash },
              );

              await setSessionCookie(ctx, { session, user });

              return {
                user,
                session,
                context: ctx,
              };
            }),
          },
        ],
      },
    },
  ],
});

type Session = Awaited<ReturnType<typeof auth.api.getSession>>;

export function isAuthenticated(
  session: Session,
): session is NonNullable<Session> {
  return session !== null && session.user !== null;
}

export function isOnboarded(session: Session) {
  return session !== null && session.user !== null && session.user.isOnboarded;
}
