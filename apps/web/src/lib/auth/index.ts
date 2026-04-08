import { betterAuth } from 'better-auth';
import { createAuthMiddleware } from 'better-auth/api';
import { nextCookies } from 'better-auth/next-js';

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

              const response = await getAuthenticatedUser()
                .then((res) => res.data)
                .catch(() => null);

              if (response === null) {
                await ctx.setSignedCookie(
                  ctx.context.authCookies.sessionToken.name,
                  '',
                  ctx.context.secret,
                  {
                    maxAge: -1,
                  },
                );

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

              const user = await ctx.context.internalAdapter.createUser({
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
