'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useParams } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { useGetProjectTags } from '@/api/__generated__/tag/tag';
import { GetProjectResponseRole } from '@/api/__generated__/types';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';
import ROUTES from '@/util/routes';

export default function ProjectTags() {
  const t = useTranslations('pages.projects.project.tags');

  const { handle } = useParams<{ handle: string }>();
  const { data: project } = useGetProjectByHandle(handle);
  const isAdmin = project?.data.role === GetProjectResponseRole.Admin;

  const { data, isPending, isError } = useGetProjectTags(handle);

  const tags = data?.data.tags ?? [];

  return (
    <section className="space-y-4">
      {isAdmin && (
        <div className="flex justify-end">
          <Button variant="outline" size="sm" asChild>
            <Link href={ROUTES.PROJECT_TAGS_MANAGE(handle)}>
              {t('manage-link')}
            </Link>
          </Button>
        </div>
      )}

      {isPending ? (
        <ProjectTagsSkeleton />
      ) : isError ? (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-destructive">{t('list.error')}</p>
        </div>
      ) : tags.length === 0 ? (
        <div className="rounded-md border px-4 py-8 text-center">
          <p className="text-sm text-muted-foreground">{t('list.empty')}</p>
        </div>
      ) : (
        <div className="flex flex-col gap-3">
          {tags.map((tag) => (
            <Link
              key={tag.id}
              href={ROUTES.PROJECT_TAG_POSTS(handle, String(tag.id))}
              className="flex items-center gap-4 rounded-md border p-4 hover:bg-accent"
            >
              <Badge variant="secondary" className="w-fit shrink-0">
                {tag.name}
              </Badge>

              <p className="flex-1 truncate text-sm text-muted-foreground">
                {tag.description || t('no-description')}
              </p>

              <p className="shrink-0 text-xs text-muted-foreground">
                {t('post-count', { count: tag.postCount })}
              </p>
            </Link>
          ))}
        </div>
      )}
    </section>
  );
}

function ProjectTagsSkeleton() {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: 6 }).map((_, index) => (
        <Skeleton key={index} className="h-16 w-full rounded-md" />
      ))}
    </div>
  );
}
