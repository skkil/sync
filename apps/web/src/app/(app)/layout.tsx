import TopNavigationBar from '@/components/layout/nav/top/TopNavigationBar';
import AppSidebar from '@/components/layout/sidebar/AppSidebar';
import ProjectContextSync from '@/components/layout/sidebar/ProjectContextSync';
import SidebarOverlay from '@/components/layout/sidebar/SidebarOverlay';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';

interface AppLayoutProps {
  children: React.ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  return (
    <SidebarProvider>
      <ProjectContextSync />
      <AppSidebar />
      <SidebarOverlay />
      <SidebarInset>
        <TopNavigationBar />
        <div className="grow w-full max-w-7xl px-7 pt-7 mx-auto">
          {children}
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}
