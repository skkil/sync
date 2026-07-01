'use client';

import { CompassIcon, HouseIcon } from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';

import { useSearchMyProjects } from '@/api/__generated__/project/project';
import {
  SidebarContent,
  SidebarFooter,
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
import { useSession } from '@/lib/auth/client';

import SidebarCloseButton from './SidebarCloseButton';

const global = [
  { label: 'Home', href: '/', icon: HouseIcon },
  { label: 'Explore', href: '/explore', icon: CompassIcon },
];

const footer = [
  { label: 'Privacy', href: '/privacy' },
  { label: 'Terms', href: '/terms' },
  { label: 'Cookies', href: '/cookies' },
];

export default function PersonalSidebarContent() {
  const pathname = usePathname();
  const router = useRouter();
  const [query] = useState('');

  const { data: session } = useSession();
  const { currentProject, setCurrentProject } = useCurrentProject();
  const { data } = useSearchMyProjects(
    { query },
    { query: { enabled: !!session } },
  );

  const projects = data?.data.projects ?? [];

  return (
    <>
      <SidebarHeader className="flex flex-row items-center justify-between p-4">
        <SidebarCloseButton />
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {global.map((item) => {
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
          <SidebarGroupLabel asChild>
            <Link href="/projects" className="hover:text-sidebar-foreground">
              Workspaces
            </Link>
          </SidebarGroupLabel>
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
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <div className="flex flex-wrap gap-x-2 gap-y-1 text-xs text-sidebar-foreground/60">
          {footer.map((link) => (
            <Link key={link.href} href={link.href} className="hover:underline">
              {link.label}
            </Link>
          ))}
        </div>
      </SidebarFooter>
    </>
  );
}
