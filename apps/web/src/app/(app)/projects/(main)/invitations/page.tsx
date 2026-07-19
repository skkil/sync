import { ArrowLeftIcon } from '@phosphor-icons/react/dist/ssr';
import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { requireSession } from '@/lib/auth/guards';
import ROUTES from '@/util/routes';

import ProjectInvitations from './_components/ProjectInvitations';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.projects.invitations');

  return { title: t('heading') };
}

export default async function ProjectInvitationsPage() {
  await requireSession();

  const t = await getTranslations('pages.projects.invitations');

  return (
    <div className="space-y-6">
      <Button variant="ghost" size="sm" asChild>
        <Link href={ROUTES.PROJECTS()}>
          <ArrowLeftIcon className="size-4" />
          {t('back')}
        </Link>
      </Button>

      <ProjectInvitations />
    </div>
  );
}
