import { getTranslations } from 'next-intl/server';
import { headers } from 'next/headers';
import Link from 'next/link';

import { BaseLayout } from '@/components/layout/app';
import { Button } from '@/components/ui/button';
import { auth } from '@/lib/auth';

interface ProjectsLayoutProps {
  children: React.ReactNode;
}

export default function ProjectsLayout({ children }: ProjectsLayoutProps) {
  return <BaseLayout left={<ProjectsLeftSidebar />}>{children}</BaseLayout>;
}

async function ProjectsLeftSidebar() {
  const t = await getTranslations('pages.projects');

  const session = await auth.api.getSession({
    headers: await headers(),
  });

  return (
    <div>
      <Link href="/projects/new">
        <Button variant="ghost" className="w-full">
          {t('new.link')}
        </Button>
      </Link>

      {session?.user && (
        <Link href={`/@${session.user.handle}/projects`}>
          <Button variant="ghost" className="w-full">
            {t('my.link')}
          </Button>
        </Link>
      )}
    </div>
  );
}
