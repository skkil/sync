import TopNavigationBar from '@/components/layout/navbar/TopNavigationBar';
import AppSidebar from '@/components/layout/sidebar/AppSidebar';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <SidebarProvider className="h-svh flex-col">
      <TopNavigationBar />
      <div className="relative flex min-h-0 flex-1 w-full">
        <AppSidebar />
        <SidebarInset className="overflow-y-auto">
          <div className="grow w-full max-w-7xl px-7 pt-7 mx-auto">
            {children}
          </div>
        </SidebarInset>
      </div>
    </SidebarProvider>
  );
}
