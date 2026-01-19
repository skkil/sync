import { NextIntlClientProvider } from 'next-intl';

interface I18nProviderProps {
  children: React.ReactNode;
}

export default function I18nProvider({ children }: I18nProviderProps) {
  return <NextIntlClientProvider>{children}</NextIntlClientProvider>;
}
