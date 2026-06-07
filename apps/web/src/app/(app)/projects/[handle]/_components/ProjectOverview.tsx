'use client';

import { useTranslations } from 'next-intl';
import { notFound } from 'next/navigation';
import { useEffect } from 'react';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import SyncError, { ErrorCode } from '@/lib/error';

interface ProjectOverviewProps {
  handle: string;
}

export default function ProjectOverview({ handle }: ProjectOverviewProps) {
  const t = useTranslations('pages.projects.project.overview');

  const { data: project, isError, error } = useGetProjectByHandle(handle);

  useEffect(() => {
    if (isError && error instanceof SyncError) {
      if (error.code === ErrorCode.PROJECT_NOT_FOUND) {
        notFound();
      }
    }
  }, [isError, error]);

  if (!project) {
    return <Skeleton className="h-48 w-full" />;
  }

  const owner = project.data.teammates.find((t) => t.isOwner);

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl">{project.data.name}</CardTitle>
        <p className="text-sm text-muted-foreground">
          {t('handle')}: {project.data.handle}
        </p>
      </CardHeader>

      <CardContent>
        <div className="flex flex-col gap-2">
          <h3 className="font-semibold">
            {t('teammates')} ({project.data.teammates.length})
          </h3>
          <ul className="flex flex-col gap-1">
            {project.data.teammates.map((teammate) => (
              <li key={teammate.id} className="text-sm text-muted-foreground">
                #{teammate.id}
                {teammate.id === owner?.id && (
                  <span className="ml-2 text-xs font-medium text-primary">
                    {t('owner')}
                  </span>
                )}
              </li>
            ))}
          </ul>
        </div>
      </CardContent>
    </Card>
  );
}
