'use client';

import {
  ArrowLeftIcon,
  FolderSimpleIcon,
  GearIcon,
  RssIcon,
  UsersIcon,
} from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
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
import { useCurrentProject } from '@/hooks/use-current-project';
import ROUTES from '@/util/routes';

import SidebarCloseButton from './SidebarCloseButton';

interface ProjectSidebarContentProps {
  handle: string;
}

export default function ProjectSidebarContent({
  handle,
}: ProjectSidebarContentProps) {
  const pathname = usePathname();
  const { currentProject } = useCurrentProject();
  const { data } = useGetProjectByHandle(handle);

  const projectName = data?.data.name ?? currentProject?.name ?? handle;

  const workspaceNavItems = [
    { label: 'Feed', href: `/projects/${handle}`, icon: RssIcon },
  ];

  return (
    <>
      <SidebarHeader className="flex flex-col gap-3 p-4">
        <div className="flex items-center justify-between">
          <Link
            href="/"
            className="flex items-center gap-1 text-xs text-sidebar-foreground/60 hover:text-sidebar-foreground"
          >
            <ArrowLeftIcon size={12} />
            Home
          </Link>
          <SidebarCloseButton />
        </div>

        <div className="flex items-center gap-2 min-w-0">
          <div className="bg-muted flex size-8 shrink-0 items-center justify-center rounded-lg">
            <FolderSimpleIcon className="size-4" />
          </div>
          <span className="truncate font-medium">{projectName}</span>
        </div>
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

        <SidebarSeparator />

        <SidebarGroup>
          <SidebarGroupLabel>Team</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  isActive={pathname === ROUTES.PROJECT_MEMBERS(handle)}
                >
                  <Link href={ROUTES.PROJECT_MEMBERS(handle)}>
                    <UsersIcon />
                    Members
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarSeparator />

        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  isActive={pathname === ROUTES.PROJECT_SETTINGS(handle)}
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
      </SidebarContent>
    </>
  );
}
