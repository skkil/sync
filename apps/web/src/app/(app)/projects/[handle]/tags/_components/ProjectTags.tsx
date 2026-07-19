'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useParams } from 'next/navigation';

import { useGetProjectByHandle } from '@/api/__generated__/project/project';
import { useGetProjectTags } from '@/api/__generated__/tag/tag';
import { GetProjectResponseRole } from '@/api/__generated__/types';
import { TagList, TagListItem } from '@/components/feature/tag/TagList';
import { Button } from '@/components/ui/button';
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

      <TagList
        tags={tags}
        getKey={(tag) => tag.id}
        isPending={isPending}
        isError={isError}
        emptyMessage={t('list.empty')}
        errorMessage={t('list.error')}
        renderItem={(tag) => (
          <TagListItem
            name={tag.name}
            description={tag.description}
            noDescriptionLabel={t('no-description')}
            postCountLabel={t('post-count', { count: tag.postCount })}
            href={ROUTES.PROJECT_TAG(handle, String(tag.id))}
          />
        )}
      />
    </section>
  );
}
