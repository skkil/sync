const CSRF_COOKIE_NAME = 'XSRF-TOKEN';

export function isServer() {
  return typeof window === 'undefined';
}

export function url(
  base: string,
  params?: Record<string, string | number | boolean | undefined | null>,
): string {
  const filteredParams =
    params &&
    Object.fromEntries(
      Object.entries(params).filter(
        ([, value]) => value !== undefined && value !== null,
      ),
    );

  const queryString = new URLSearchParams(
    Object.entries(filteredParams || {}).reduce(
      (params: Record<string, string>, [key, value]) => {
        if (value !== undefined && value !== null) {
          params[key] = String(value);
        }
        return params;
      },
      {},
    ),
  ).toString();

  return `${base}${queryString.length === 0 ? '' : '?'}${queryString}`;
}

export async function getCookies() {
  if (isServer()) {
    try {
      const { cookies } = await import('next/headers');
      const cookieStore = await cookies();
      const allCookies = cookieStore.getAll();

      return allCookies
        .map((cookie) => `${cookie.name}=${cookie.value}`)
        .join('; ');
    } catch {
      return undefined;
    }
  }

  return undefined;
}

export async function getCsrfToken(): Promise<string | undefined> {
  if (isServer()) {
    try {
      const { cookies } = await import('next/headers');
      const cookieStore = await cookies();
      return cookieStore.get(CSRF_COOKIE_NAME)?.value;
    } catch {
      return undefined;
    }
  } else {
    const value = document.cookie
      .split('; ')
      .find((row) => row.startsWith(`${CSRF_COOKIE_NAME}=`))
      ?.split('=')[1];
    return value ? decodeURIComponent(value) : undefined;
  }
}
