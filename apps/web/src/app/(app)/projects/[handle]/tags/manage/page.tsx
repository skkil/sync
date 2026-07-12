import { ArrowLeft } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import ROUTES from '@/util/routes';

import ManageProjectTags from './_components/ManageProjectTags';

interface ProjectTagsManagePageProps {
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProjectTagsManagePage({
  params,
}: ProjectTagsManagePageProps) {
  const { handle } = await params;
  const t = await getTranslations('pages.projects.project.tags.manage');

  return (
    <section className="space-y-6">
      <Button variant="ghost" size="sm" asChild>
        <Link href={ROUTES.PROJECT_TAGS(handle)}>
          <ArrowLeft className="size-4" />
          {t('back')}
        </Link>
      </Button>

      <ManageProjectTags />
    </section>
  );
}
