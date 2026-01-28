import I18nProvider from './I18nProvider';
import QueryProvider from './QueryProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <I18nProvider>
      <QueryProvider>{children}</QueryProvider>
    </I18nProvider>
  );
}
