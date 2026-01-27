import I18nProvider from './I18nProvider';
import ModalProvider from './ModalProvider';
import { StoreProvider } from './StoreProvider';

interface AppProviderProps {
  children?: React.ReactNode;
}

export default function AppProvider({ children }: AppProviderProps) {
  return (
    <StoreProvider>
      <I18nProvider>
        {children}
        <ModalProvider />
      </I18nProvider>
    </StoreProvider>
  );
}
