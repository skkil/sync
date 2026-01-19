import I18nProvider from './I18nProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return <I18nProvider>{children}</I18nProvider>;
}
