import { getSessionCookie } from 'better-auth/cookies';
import { NextRequest, NextResponse } from 'next/server';

import ROUTES from '@/util/routes';

export function proxy(request: NextRequest) {
  const sessionCookie = getSessionCookie(request);

  if (!sessionCookie) {
    return NextResponse.redirect(new URL(ROUTES.ABOUT(), request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/'],
};
