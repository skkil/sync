import { v4 as uuidv4 } from 'uuid';

const CSRF_COOKIE_NAME = 'XSRF-TOKEN';

async function getCookieValueServer(name: string): Promise<string | undefined> {
  try {
    const { cookies } = await import('next/headers');
    const cookieStore = await cookies();
    return cookieStore.get(name)?.value;
  } catch {
    return undefined;
  }
}

function getCookieValue(name: string): string | undefined {
  const value = document.cookie
    .split('; ')
    .find((row) => row.startsWith(`${name}=`))
    ?.split('=')[1];

  return value ? decodeURIComponent(value) : undefined;
}

function processCsrfToken(csrfToken: string): string {
  const randomUUID = uuidv4();
  let secretString = '';

  for (let i = 0; i < csrfToken.length; i++) {
    const targetChar = csrfToken.charCodeAt(i);
    const randomChar = randomUUID.charCodeAt(i);
    const xorChar = targetChar ^ randomChar;
    secretString += String.fromCharCode(xorChar);
  }

  return btoa(randomUUID + secretString);
}

export async function getCsrfTokenServer(): Promise<string> {
  const csrfToken = await getCookieValueServer(CSRF_COOKIE_NAME);
  if (!csrfToken) {
    return '';
  }

  return processCsrfToken(csrfToken);
}

export function getCsrfToken() {
  const csrfToken = getCookieValue(CSRF_COOKIE_NAME);
  if (!csrfToken) {
    return '';
  }

  return processCsrfToken(csrfToken);
}
