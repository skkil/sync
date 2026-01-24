import { betterAuth } from 'better-auth';
import { createAuthMiddleware } from 'better-auth/api';
import { nextCookies } from 'better-auth/next-js';

import { env } from '../env';

export const auth = betterAuth({
  plugins: [
    nextCookies(),
    {
      id: 'spring-session',
      hooks: {
        after: [
          {
            matcher: () => true,
            handler: createAuthMiddleware(async (ctx) => {
              const existingSession = await ctx.getSignedCookie(
                ctx.context.authCookies.sessionToken.name,
                ctx.context.secret,
              );

              if (existingSession) {
                return {
                  context: ctx,
                };
              }

              const sessionCookie = ctx.getCookie('session');
              if (!sessionCookie) {
                return null;
              }

              const res = await fetch(
                `${env.NEXT_PUBLIC_BACKEND_URL}/users/me`,
                {
                  method: 'GET',
                  headers: {
                    Cookie: `session=${sessionCookie}`,
                  },
                },
              );
              const { userId, fullName, email } = await res.json();

              const user = await ctx.context.internalAdapter.createUser({
                id: userId,
                name: fullName,
                email,
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
