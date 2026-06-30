'use client';

import { HouseIcon, PlusIcon, XIcon } from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';

import { useSearchMyProjects } from '@/api/__generated__/project/project';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarSeparator,
  useSidebar,
} from '@/components/ui/sidebar';
import { useCurrentProject } from '@/hooks/use-current-project';
import { useSession } from '@/lib/auth/client';

const globalNavItems = [{ label: '홈', href: '/', icon: HouseIcon }];

export default function AppSidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const [query] = useState('');
  const { setOpen } = useSidebar();

  const { data: session } = useSession();
  const { currentProject, setCurrentProject } = useCurrentProject();
  const { data } = useSearchMyProjects(
    { query },
    { query: { enabled: !!session } },
  );

  const projects = data?.data.projects ?? [];

  return (
    <Sidebar variant="floating">
      <SidebarHeader className="flex flex-row items-center justify-between p-4">
        <Link href="/">
          <Logo />
        </Link>
        <Button
          variant="ghost"
          size="icon"
          className="size-7 shrink-0"
          onClick={() => setOpen(false)}
        >
          <XIcon />
        </Button>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {globalNavItems.map((item) => {
                const Icon = item.icon;
                const isActive = pathname === item.href;
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
          <SidebarGroupLabel>프로젝트</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {projects.map((project) => {
                const isActive = currentProject?.handle === project.handle;
                return (
                  <SidebarMenuItem key={project.id}>
                    <SidebarMenuButton
                      isActive={isActive}
                      onClick={() => {
                        setCurrentProject({
                          handle: project.handle,
                          name: project.name,
                        });
                        router.push(`/projects/${project.handle}`);
                      }}
                    >
                      {project.name}
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}

              <SidebarMenuItem>
                <SidebarMenuButton asChild>
                  <Link href="/projects/new">
                    <PlusIcon />새 프로젝트
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
