'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetProjectsByUser } from '@/api/__generated__/project/project';
import { Skeleton } from '@/components/ui/skeleton';

interface ProfileProjectsListProps {
  handle: string;
}

export default function ProfileProjectsList({
  handle,
}: ProfileProjectsListProps) {
  const t = useTranslations('pages.profile.projects');

  const { data, isPending, isError } = useGetProjectsByUser(handle);
  const projects = data?.data.projects ?? [];

  if (isPending) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 3 }).map((_, i) => (
          <Skeleton key={i} className="h-16 w-full" />
        ))}
      </div>
    );
  }

  if (isError) {
    return (
      <div className="rounded-md border px-4 py-8 text-center">
        <p className="text-sm text-destructive">{t('list.error')}</p>
      </div>
    );
  }

  if (projects.length === 0) {
    return (
      <div className="rounded-md border px-4 py-8 text-center">
        <p className="text-sm text-muted-foreground">{t('list.empty')}</p>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-3">
      {projects.map((project) => (
        <Link key={project.id} href={`/projects/${project.handle}`}>
          <article className="rounded-md border p-4 hover:bg-accent transition-colors">
            <p className="font-medium">{project.name}</p>
            <p className="text-sm text-muted-foreground">/{project.handle}</p>
          </article>
        </Link>
      ))}
    </div>
  );
}
