'use client';

import {
  BookmarkSimpleIcon,
  CompassIcon,
  FileTextIcon,
  HouseIcon,
  PencilSimpleIcon,
  PlusIcon,
  TagIcon,
  TrendUpIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

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
import { useMounted } from '@/hooks/use-mounted';
import { useRequireAuth } from '@/hooks/use-require-auth';
import { isAuthenticated } from '@/lib/auth';
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import SidebarCloseButton from './SidebarCloseButton';

const MAX_VISIBLE_PROJECTS = 5;

const nav = [
  {
    labelKey: 'nav.home',
    href: ROUTES.HOME(),
    icon: HouseIcon,
    authenticated: false,
  },
  {
    labelKey: 'nav.trending-posts',
    href: ROUTES.EXPLORE_TRENDING(),
    icon: TrendUpIcon,
    authenticated: false,
  },
  {
    labelKey: 'nav.explore-projects',
    href: ROUTES.EXPLORE_PROJECTS(),
    icon: CompassIcon,
    authenticated: false,
  },
  {
    labelKey: 'nav.tags',
    href: ROUTES.EXPLORE_TAGS(),
    icon: TagIcon,
    authenticated: false,
  },
] as const;

const yours = [
  {
    labelKey: 'nav.drafts',
    href: ROUTES.DRAFTS(),
    icon: FileTextIcon,
    authenticated: true,
  },
  {
    // Reuses `components.navigation.menu.bookmarks` — same bookmarks concept
    // already translated for the top navigation bar.
    labelKey: 'menu.bookmarks',
    namespace: 'navigation' as const,
    href: ROUTES.BOOKMARKS(),
    icon: BookmarkSimpleIcon,
    authenticated: true,
  },
] as const;

const footer = [
  { labelKey: 'privacy', href: ROUTES.PRIVACY() },
  { labelKey: 'terms', href: ROUTES.TERMS() },
  { labelKey: 'cookies', href: ROUTES.COOKIES() },
] as const;

export default function PersonalSidebarContent() {
  const t = useTranslations('components.layout.sidebar');
  const tNav = useTranslations('components.navigation');
  const tFooter = useTranslations('components.footer');
  const pathname = usePathname();
  const [query] = useState('');
  const { requireAuth } = useRequireAuth();

  // `useSession` can resolve synchronously from its client-side cache before
  // hydration, while SSR always renders a logged-out state. Gating on
  // `mounted` keeps the first client render identical to the server-rendered
  // HTML so the project list doesn't shift Radix's useId-based ids and cause
  // a hydration mismatch.
  const mounted = useMounted();

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
              <SidebarMenuItem>
                <SidebarMenuButton
                  asChild
                  className="bg-success-tint text-success-text hover:bg-success-tint/80 active:bg-success-tint/70"
                >
                  <Link
                    href={ROUTES.NEW_POST()}
                    onClick={(event) => {
                      if (
                        !requireAuth({
                          intent: 'write',
                          redirectTo: ROUTES.NEW_POST(),
                        })
                      ) {
                        event.preventDefault();
                      }
                    }}
                  >
                    <PencilSimpleIcon />
                    {t('ask-write')}
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {nav.map((item) => {
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
                        {t(item.labelKey)}
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
          <SidebarGroupLabel>{t('yours')}</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {yours.map((item) => {
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
                        {'namespace' in item
                          ? tNav(item.labelKey)
                          : t(item.labelKey)}
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
                    {t('projects')}
                  </Link>

                  <LinkButton href={ROUTES.NEW_PROJECT()} variant="ghost">
                    <PlusIcon />
                  </LinkButton>
                </div>
              </SidebarGroupLabel>
              <SidebarGroupContent>
                <SidebarMenu>
                  {projects.slice(0, MAX_VISIBLE_PROJECTS).map((project) => {
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
                  {projects.length > MAX_VISIBLE_PROJECTS && (
                    <SidebarMenuItem>
                      <SidebarMenuButton asChild>
                        <Link
                          href={ROUTES.PROJECTS()}
                          className="text-sidebar-foreground/60"
                        >
                          {t('see-more')}
                        </Link>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  )}
                </SidebarMenu>
              </SidebarGroupContent>
            </SidebarGroup>
          </>
        )}
      </SidebarContent>

      <SidebarFooter className="p-4">
        <div className="flex flex-wrap gap-x-3 gap-y-1 text-xs text-sidebar-foreground/60">
          {footer.map((link) => (
            <Link key={link.href} href={link.href} className="hover:underline">
              {tFooter(link.labelKey)}
            </Link>
          ))}
        </div>
      </SidebarFooter>
    </>
  );
}
