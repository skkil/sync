'use client';

import { usePathname } from 'next/navigation';

import { Sidebar } from '@/components/ui/sidebar';

import PersonalSidebarContent from './PersonalSidebarContent';
import ProjectSidebarContent from './ProjectSidebarContent';

function getProjectHandleFromPathname(pathname: string): string | null {
  const match = pathname.match(/^\/projects\/([^/]+)/);
  if (!match) return null;

  const handle = match[1];
  if (!handle || handle === 'new') return null;

  return handle;
}

export default function AppSidebar() {
  const pathname = usePathname();
  const projectHandle = getProjectHandleFromPathname(pathname);

  return (
    <Sidebar>
      {projectHandle ? (
        <ProjectSidebarContent handle={projectHandle} />
      ) : (
        <PersonalSidebarContent />
      )}

      <div className="mb-4">
        <p className="px-3 text-xs text-sidebar-foreground/40">
          © {new Date().getFullYear()} SKKiL.
        </p>
      </div>
    </Sidebar>
  );
}
