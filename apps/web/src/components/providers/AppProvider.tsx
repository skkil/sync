import { TooltipProvider } from '@/components/ui/tooltip';

import I18nProvider from './I18nProvider';
import ModalProvider from './ModalProvider';
import QueryProvider from './QueryProvider';
import { StoreProvider } from './StoreProvider';
import ThemeProvider from './ThemeProvider';
import WebSocketProvider from './WebSocketProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <ThemeProvider>
      <I18nProvider>
        <WebSocketProvider>
          <StoreProvider>
            <QueryProvider>
              <TooltipProvider>{children}</TooltipProvider>
              <ModalProvider />
            </QueryProvider>
          </StoreProvider>
        </WebSocketProvider>
      </I18nProvider>
    </ThemeProvider>
  );
}
