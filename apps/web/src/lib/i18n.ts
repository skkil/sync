import { getRequestConfig } from 'next-intl/server';
import { cookies } from 'next/headers';

export async function getLocaleFromCookie() {
  const cookieStore = await cookies();
  return cookieStore.get('locale')?.value || 'ko';
}

export default getRequestConfig(async () => {
  const locale = await getLocaleFromCookie();

  return {
    locale,
    messages: (await import(`../../public/locales/${locale}.json`)).default,
  };
});
