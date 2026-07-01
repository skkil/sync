'use client';

import { usePathname } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuSkeleton,
} from '@/components/ui/sidebar';
import { Skeleton } from '@/components/ui/skeleton';

import PersonalSidebarContent from './PersonalSidebarContent';
import ProjectSidebarContent from './ProjectSidebarContent';

export default function AppSidebar() {
  const pathname = usePathname();
  const match = pathname.match(/^\/projects\/(?!new(?:\/|$))([^/]+)/);
  const handle = match?.[1];

  const { isPending, isError } = useGetProjectByHandle(handle ?? '', {
    query: { enabled: !!handle },
  });

  return (
    <Sidebar>
      {!handle || isError ? (
        <PersonalSidebarContent />
      ) : isPending ? (
        <ProjectSidebarSkeleton />
      ) : (
        <ProjectSidebarContent handle={handle} />
      )}

      <div className="mb-4">
        <p className="px-3 text-xs text-sidebar-foreground/40">
          © {new Date().getFullYear()} SKKiL.
        </p>
      </div>
    </Sidebar>
  );
}

function ProjectSidebarSkeleton() {
  return (
    <>
      <SidebarHeader className="flex flex-col gap-3 p-4">
        <Skeleton className="h-3 w-16" />

        <div className="flex items-center gap-2">
          <Skeleton className="size-8 shrink-0 rounded-lg" />
          <Skeleton className="h-4 w-24" />
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {Array.from({ length: 3 }).map((_, index) => (
                <SidebarMenuItem key={index}>
                  <SidebarMenuSkeleton showIcon />
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </>
  );
}
