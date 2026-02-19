import { betterAuth } from 'better-auth';
import { createAuthMiddleware } from 'better-auth/api';
import { nextCookies } from 'better-auth/next-js';
import ky from 'ky';

import { env } from '../env';

export interface GetAuthenticatedUserResponse {
  userId: string;
  fullName: string;
  email: string;
  profileImageUrl: string | null;
  isOnboarded: boolean;
  role: 'USER' | 'ADMIN';
}

export const auth = betterAuth({
  user: {
    additionalFields: {
      isOnboarded: {
        type: 'boolean',
        input: false,
      },
      role: {
        type: 'string',
        input: false,
        defaultValue: 'USER',
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

              const response = await ky
                .get<GetAuthenticatedUserResponse>(
                  `${env.NEXT_PUBLIC_BACKEND_URL}/users/me`,
                  {
                    headers: {
                      Cookie: `session=${sessionCookie}`,
                    },
                  },
                )
                .then((res) => res.json())
                .catch(() => null);

              if (response === null) {
                return null;
              }

              const { userId, fullName, email, profileImageUrl, isOnboarded, role } = response;

              const user = await ctx.context.internalAdapter.createUser({
                id: userId,
                name: fullName,
                email,
                image: profileImageUrl,
                isOnboarded,
                role,
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
