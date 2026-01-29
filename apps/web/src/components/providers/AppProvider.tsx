import I18nProvider from './I18nProvider';
import ModalProvider from './ModalProvider';
import QueryProvider from './QueryProvider';
import { StoreProvider } from './StoreProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <StoreProvider>
      <I18nProvider>
        <QueryProvider>
          {children}
          <ModalProvider />
        </QueryProvider>
      </I18nProvider>
    </StoreProvider>
  );
}
