import { EnvelopeSimpleIcon, PlusIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { LinkButton } from '@/components/ui/button';
import { requireSession } from '@/lib/auth/guards';
import ROUTES from '@/util/routes';

import FollowingProjects from './_components/FollowingProjects';
import UserProjects from './_components/UserProjects';

export default async function Projects() {
  await requireSession();
  const t = await getTranslations('pages.projects.list');

  return (
    <div className="space-y-8">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <h1 className="text-2xl font-bold">{t('title')}</h1>
          <p className="text-muted-foreground text-sm">{t('description')}</p>
        </div>

        <div className="flex items-center gap-2">
          <LinkButton href={ROUTES.PROJECT_INVITATIONS()} variant="outline">
            <EnvelopeSimpleIcon />
            {t('invitations')}
          </LinkButton>

          <LinkButton href={ROUTES.NEW_PROJECT()}>
            <PlusIcon />
            {t('create')}
          </LinkButton>
        </div>
      </div>

      <section className="space-y-3">
        <h2 className="text-muted-foreground text-xs font-semibold tracking-wide uppercase">
          {t('my-projects')}
        </h2>

        <UserProjects />
      </section>

      <section className="space-y-3">
        <div className="flex items-center justify-between gap-3 rounded-lg p-4">
          <h2 className="text-muted-foreground text-xs font-semibold tracking-wide uppercase">
            {t('following.heading')}
          </h2>

          <Link
            href={ROUTES.EXPLORE_PROJECTS()}
            className="text-primary inline-block text-sm font-medium hover:underline"
          >
            {t('following.explore-more')}
          </Link>
        </div>

        <FollowingProjects />
      </section>
    </div>
  );
}
