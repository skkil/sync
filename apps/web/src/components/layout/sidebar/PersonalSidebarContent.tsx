'use client';

import { CompassIcon, HouseIcon, NotePencilIcon } from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';

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
import { useSession } from '@/lib/auth/client';

import SidebarCloseButton from './SidebarCloseButton';

const global = [
  { label: 'Home', href: '/', icon: HouseIcon },
  { label: 'Explore', href: '/explore', icon: CompassIcon },
  { label: 'New Post', href: '/posts/new', icon: NotePencilIcon },
];

const footer = [
  { label: 'Privacy', href: '/privacy' },
  { label: 'Terms', href: '/terms' },
  { label: 'Cookies', href: '/cookies' },
];

export default function PersonalSidebarContent() {
  const pathname = usePathname();
  const [query] = useState('');

  // `useSession` can resolve synchronously from its client-side cache before
  // hydration, while SSR always renders a logged-out state. Gating on
  // `mounted` keeps the first client render identical to the server-rendered
  // HTML so the project list doesn't shift Radix's useId-based ids and cause
  // a hydration mismatch.
  const [mounted, setMounted] = useState(false);
  useEffect(() => {
    setMounted(true);
  }, []);

  const { data: session } = useSession();
  const { data } = useSearchMyProjects(
    { query },
    { query: { enabled: mounted && !!session } },
  );

  const projects = mounted ? (data?.data.projects ?? []) : [];

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
                const isActive = pathname === `/projects/${project.handle}`;
                return (
                  <SidebarMenuItem key={project.id}>
                    <SidebarMenuButton asChild isActive={isActive}>
                      <Link href={`/projects/${project.handle}`}>
                        {project.name}
                      </Link>
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
