'use client';

import {
  BookOpenIcon,
  BookmarkSimpleIcon,
  CaretDownIcon,
  ChatCircleIcon,
  DotsThreeIcon,
  GearIcon,
  HouseIcon,
  NotePencilIcon,
  PencilIcon,
  QuestionIcon,
  RssIcon,
  TagIcon,
} from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { usePathname, useSearchParams } from 'next/navigation';
import { useState } from 'react';

import {
  useGetProjectByHandle,
  useSearchMyProjects,
} from '@/api/__generated__/project/project';
import { ProjectAvatar } from '@/components/feature/project/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
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
import { useSession } from '@/lib/auth/client';
import ROUTES from '@/util/routes';

import SidebarCloseButton from './SidebarCloseButton';

interface ProjectSidebarContentProps {
  handle: string;
}

export default function ProjectSidebarContent({
  handle,
}: ProjectSidebarContentProps) {
  const { isAuthenticated } = useRequireAuth();
  const { data: projectData } = useGetProjectByHandle(handle);
  const isViewer = !!projectData?.data.isViewer;

  return (
    <>
      <SidebarHeader>
        <div className="flex items-center justify-end">
          <SidebarCloseButton />
        </div>

        <ProjectSwitcher handle={handle} />

        {isViewer && <AskOrWriteButton handle={handle} />}
      </SidebarHeader>

      <SidebarContent>
        <Browse handle={handle} />

        {isAuthenticated && (
          <>
            <SidebarSeparator />
            <MyContributions handle={handle} isViewer={isViewer} />

            {isViewer && (
              <>
                <SidebarSeparator />
                <Settings handle={handle} />
              </>
            )}
          </>
        )}
      </SidebarContent>
    </>
  );
}

interface SectionProps {
  handle: string;
}

const PROJECT_SWITCHER_VISIBLE_COUNT = 5;

function ProjectSwitcher({ handle }: SectionProps) {
  const t = useTranslations('components.layout.sidebar');
  const [isProjectMenuOpen, setIsProjectMenuOpen] = useState(false);
  const { isAuthenticated } = useRequireAuth();
  const { data } = useGetProjectByHandle(handle);
  const { data: myProjectsData } = useSearchMyProjects(
    { query: '' },
    { query: { enabled: isAuthenticated } },
  );

  const projectName = data?.data.summary.name ?? handle;
  const projectIconUrl = data?.data.summary.iconUrl;
  const myProjects = myProjectsData?.data.projects ?? [];
  const visibleProjects = myProjects.slice(0, PROJECT_SWITCHER_VISIBLE_COUNT);
  const hasMoreProjects = myProjects.length > PROJECT_SWITCHER_VISIBLE_COUNT;

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu
          open={isProjectMenuOpen}
          onOpenChange={setIsProjectMenuOpen}
        >
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="rounded-lg border border-sidebar-border"
              data-state={isProjectMenuOpen ? 'open' : 'closed'}
            >
              <ProjectAvatar name={projectName} iconUrl={projectIconUrl} />
              <span className="truncate font-medium">{projectName}</span>
              <CaretDownIcon
                className={`ml-auto transition-transform ${
                  isProjectMenuOpen ? 'rotate-180' : ''
                }`}
              />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="w-64">
            <DropdownMenuLabel>{t('your-projects')}</DropdownMenuLabel>
            {visibleProjects.map((project) => (
              <DropdownMenuItem key={project.handle} asChild>
                <Link href={ROUTES.PROJECT(project.handle)}>
                  <ProjectAvatar
                    name={project.name}
                    iconUrl={project.iconUrl}
                    size="sm"
                  />
                  <span className="truncate">{project.name}</span>
                </Link>
              </DropdownMenuItem>
            ))}
            {hasMoreProjects && (
              <DropdownMenuItem asChild>
                <Link href={ROUTES.PROJECTS()}>
                  <DotsThreeIcon />
                  <span className="truncate">{t('see-more')}</span>
                </Link>
              </DropdownMenuItem>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  );
}

function AskOrWriteButton({ handle }: SectionProps) {
  const t = useTranslations('components.layout.sidebar');
  const pathname = usePathname();
  const { requireAuth } = useRequireAuth();

  return (
    <SidebarMenu>
      <SidebarMenuButton
        asChild
        isActive={pathname === ROUTES.NEW_PROJECT_POST(handle)}
        className="bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary data-[active=true]:bg-primary/20 data-[active=true]:text-primary"
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
          {t('ask-write')}
        </Link>
      </SidebarMenuButton>
    </SidebarMenu>
  );
}

function Browse({ handle }: SectionProps) {
  const t = useTranslations('components.layout.sidebar');
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const isPostsPath = pathname === ROUTES.PROJECT_POSTS(handle);
  const type = searchParams.get('type');
  const authorHandle = searchParams.get('authorHandle');
  const isTagsPath = pathname.startsWith(ROUTES.PROJECT_TAGS(handle));

  const items = [
    {
      labelKey: 'nav.home',
      href: ROUTES.PROJECT(handle),
      icon: HouseIcon,
      isActive: pathname === ROUTES.PROJECT(handle),
    },
    {
      labelKey: 'nav.feed',
      href: ROUTES.PROJECT_FEED(handle),
      icon: RssIcon,
      isActive: isPostsPath && !type && !authorHandle,
    },
    {
      labelKey: 'nav.questions',
      href: ROUTES.PROJECT_QUESTIONS(handle),
      icon: QuestionIcon,
      isActive: isPostsPath && type === 'QUESTION',
    },
    {
      labelKey: 'nav.guides',
      href: ROUTES.PROJECT_GUIDES(handle),
      icon: BookOpenIcon,
      isActive: isPostsPath && type === 'LONG',
    },
    {
      labelKey: 'nav.tags',
      href: ROUTES.PROJECT_TAGS(handle),
      icon: TagIcon,
      isActive: isTagsPath,
    },
  ] as const;

  return (
    <SidebarGroup>
      <SidebarGroupLabel>{t('nav.browse')}</SidebarGroupLabel>
      <SidebarGroupContent>
        <SidebarMenu>
          {items.map((item) => {
            const Icon = item.icon;
            return (
              <SidebarMenuItem key={item.labelKey}>
                <SidebarMenuButton asChild isActive={item.isActive}>
                  <Link href={item.href}>
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
  );
}

interface MyContributionsProps extends SectionProps {
  isViewer: boolean;
}

function MyContributions({ handle, isViewer }: MyContributionsProps) {
  const t = useTranslations('components.layout.sidebar');
  const pathname = usePathname();
  const { data: session } = useSession();

  const myHandle = session?.user.handle;
  if (!myHandle) {
    return null;
  }

  const items = [
    {
      labelKey: 'nav.my-posts',
      href: ROUTES.PROJECT_MY_POSTS(handle),
      icon: NotePencilIcon,
      isActive: pathname === ROUTES.PROJECT_MY_POSTS(handle),
    },
    {
      labelKey: 'nav.my-comments',
      href: ROUTES.PROJECT_MY_COMMENTS(handle),
      icon: ChatCircleIcon,
      isActive: pathname === ROUTES.PROJECT_MY_COMMENTS(handle),
    },
    ...(isViewer
      ? ([
          {
            labelKey: 'nav.bookmarks',
            href: ROUTES.PROJECT_BOOKMARKS(handle),
            icon: BookmarkSimpleIcon,
            isActive: pathname === ROUTES.PROJECT_BOOKMARKS(handle),
          },
          {
            labelKey: 'nav.drafts',
            href: ROUTES.PROJECT_DRAFTS(handle),
            icon: NotePencilIcon,
            isActive: pathname === ROUTES.PROJECT_DRAFTS(handle),
          },
        ] as const)
      : []),
  ] as const;

  return (
    <SidebarGroup>
      <SidebarGroupLabel>{t('my-contributions')}</SidebarGroupLabel>
      <SidebarGroupContent>
        <SidebarMenu>
          {items.map((item) => {
            const Icon = item.icon;
            return (
              <SidebarMenuItem key={item.labelKey}>
                <SidebarMenuButton asChild isActive={item.isActive}>
                  <Link href={item.href}>
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
  );
}

function Settings({ handle }: SectionProps) {
  const t = useTranslations('components.layout.sidebar');
  const pathname = usePathname();

  return (
    <SidebarGroup>
      <SidebarGroupContent>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton
              asChild
              isActive={pathname.startsWith(ROUTES.PROJECT_SETTINGS(handle))}
            >
              <Link href={ROUTES.PROJECT_SETTINGS(handle)}>
                <GearIcon />
                {t('nav.settings')}
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarGroupContent>
    </SidebarGroup>
  );
}
