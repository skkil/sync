import { betterAuth } from 'better-auth';
import { createAuthMiddleware } from 'better-auth/api';
import { nextCookies } from 'better-auth/next-js';
import { HTTPError } from 'ky';

import { getUserPreferences } from '@/api/__generated__/preferences/preferences';
import { getAuthenticatedUser } from '@/api/__generated__/profile/profile';

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

              const existingToken = await ctx.getSignedCookie(
                ctx.context.authCookies.sessionToken.name,
                ctx.context.secret,
              );

              if (existingToken) {
                const existingSession =
                  await ctx.context.internalAdapter.findSession(existingToken);

                if (
                  existingSession &&
                  new Date(existingSession.session.expiresAt) > new Date()
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
              );

              await ctx.setSignedCookie(
                ctx.context.authCookies.sessionToken.name,
                session.token,
                ctx.context.secret,
                {
                  ...ctx.context.authCookies.sessionToken.attributes,
                },
              );

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
