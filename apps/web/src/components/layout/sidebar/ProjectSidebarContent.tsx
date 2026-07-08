'use client';

import {
  ArrowLeftIcon,
  GearIcon,
  PencilIcon,
  RssIcon,
} from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import {
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarSeparator,
} from '@/components/ui/sidebar';
import { useRequireAuth } from '@/hooks/use-require-auth';
import ROUTES from '@/util/routes';

import SidebarCloseButton from './SidebarCloseButton';

interface ProjectSidebarContentProps {
  handle: string;
}

export default function ProjectSidebarContent({
  handle,
}: ProjectSidebarContentProps) {
  const pathname = usePathname();
  const { requireAuth, isAuthenticated } = useRequireAuth();
  const { data } = useGetProjectByHandle(handle);

  const projectName = data?.data.summary.name ?? handle;
  const projectIconUrl = data?.data.summary.iconUrl;

  const workspaceNavItems = [
    { label: 'Feed', href: ROUTES.PROJECT_POSTS(handle), icon: RssIcon },
  ];

  return (
    <>
      <SidebarHeader className="flex flex-col gap-3 p-4">
        <div className="flex items-center justify-between">
          {isAuthenticated ? (
            <Link
              href={ROUTES.HOME()}
              className="flex items-center gap-1 text-xs text-sidebar-foreground/60 hover:text-sidebar-foreground"
            >
              <ArrowLeftIcon size={12} />
              Home
            </Link>
          ) : (
            <div />
          )}
          <SidebarCloseButton />
        </div>

        <SidebarMenu>
          <SidebarMenuButton
            asChild
            size="lg"
            isActive={pathname === ROUTES.PROJECT(handle)}
          >
            <Link href={ROUTES.PROJECT(handle)}>
              <ProjectAvatar name={projectName} iconUrl={projectIconUrl} />
              <span className="truncate font-medium">{projectName}</span>
            </Link>
          </SidebarMenuButton>

          <SidebarMenuButton
            asChild
            isActive={pathname === ROUTES.NEW_PROJECT_POST(handle)}
          >
            <Link
              href={ROUTES.NEW_PROJECT_POST(handle)}
              onClick={(event) => {
                if (
                  !requireAuth({
                    intent: 'write',
                    redirectTo: ROUTES.NEW_PROJECT_POST(handle),
                  })
                ) {
                  event.preventDefault();
                }
              }}
            >
              <PencilIcon />
              Write a new post
            </Link>
          </SidebarMenuButton>
        </SidebarMenu>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Workspace</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {workspaceNavItems.map((item) => {
                const Icon = item.icon;
                const isActive = pathname === item.href.split('?')[0];
                return (
                  <SidebarMenuItem key={item.href}>
                    <SidebarMenuButton asChild isActive={isActive}>
                      <Link href={item.href}>
                        <Icon />
                        {item.label}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        {isAuthenticated && (
          <>
            <SidebarSeparator />

            <SidebarGroup>
              <SidebarGroupContent>
                <SidebarMenu>
                  <SidebarMenuItem>
                    <SidebarMenuButton
                      asChild
                      isActive={pathname.startsWith(
                        ROUTES.PROJECT_SETTINGS(handle),
                      )}
                    >
                      <Link href={ROUTES.PROJECT_SETTINGS(handle)}>
                        <GearIcon />
                        Settings
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </>
        )}
      </SidebarContent>
    </>
  );
}
