import I18nProvider from './I18nProvider';
import ModalProvider from './ModalProvider';
import QueryProvider from './QueryProvider';
import { StoreProvider } from './StoreProvider';
import WebSocketProvider from './WebSocketProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <I18nProvider>
      <WebSocketProvider>
        <StoreProvider>
          <QueryProvider>
            {children}
            <ModalProvider />
          </QueryProvider>
        </StoreProvider>
      </WebSocketProvider>
    </I18nProvider>
  );
}
