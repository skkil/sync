'use client';

import {
  BookmarkSimpleIcon,
  CompassIcon,
  HouseIcon,
  NotePencilIcon,
  PlusIcon,
} from '@phosphor-icons/react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';

import { useSearchMyProjects } from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import { LinkButton } from '@/components/ui/button';
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
import { useRequireAuth } from '@/hooks/use-require-auth';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import SidebarCloseButton from './SidebarCloseButton';

const global = [
  { label: 'Home', href: ROUTES.HOME(), icon: HouseIcon, authenticated: false },
  {
    label: 'Explore',
    href: ROUTES.EXPLORE(),
    icon: CompassIcon,
    authenticated: false,
  },
  {
    label: 'New Post',
    href: ROUTES.NEW_POST(),
    icon: NotePencilIcon,
    authenticated: true,
  },
  {
    label: 'Bookmarks',
    href: ROUTES.BOOKMARKS(),
    icon: BookmarkSimpleIcon,
    authenticated: true,
  },
];

const footer = [
  { label: 'Privacy', href: ROUTES.PRIVACY() },
  { label: 'Terms', href: ROUTES.TERMS() },
  { label: 'Cookies', href: ROUTES.COOKIES() },
];

export default function PersonalSidebarContent() {
  const pathname = usePathname();
  const [query] = useState('');
  const { requireAuth } = useRequireAuth();

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
                      <Link
                        href={item.href}
                        onClick={(event) => {
                          if (
                            item.authenticated &&
                            !requireAuth({
                              intent: 'write',
                              redirectTo: item.href,
                            })
                          ) {
                            event.preventDefault();
                          }
                        }}
                      >
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

        {isAuthenticated(session) && (
          <>
            <SidebarSeparator />
            <SidebarGroup>
              <SidebarGroupLabel asChild>
                <div className="flex">
                  <Link
                    href={ROUTES.PROJECTS()}
                    className="grow hover:text-sidebar-foreground"
                  >
                    Workspaces
                  </Link>

                  <LinkButton href={ROUTES.NEW_PROJECT()} variant="ghost">
                    <PlusIcon />
                  </LinkButton>
                </div>
              </SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {projects.map((project) => {
                    const isActive =
                      pathname === ROUTES.PROJECT(project.handle);
                    return (
                      <SidebarMenuItem key={project.handle}>
                        <SidebarMenuButton asChild isActive={isActive}>
                          <Link href={ROUTES.PROJECT(project.handle)}>
                            <ProjectAvatar
                              name={project.name}
                              iconUrl={project.iconUrl}
                              size="sm"
                            />
                            {project.name}
                          </Link>
                        </SidebarMenuButton>
                      </SidebarMenuItem>
                    );
                  })}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </>
        )}
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
