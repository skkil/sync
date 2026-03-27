import { getTranslations } from 'next-intl/server';

import ProfileOverview from './_components/ProfileOverview';
import ProfileTabs from './_components/ProfileTabs';

interface ProfileLayoutProps {
  content: React.ReactNode;
  params: Promise<{
    handle: string;
  }>;
}

export default async function ProfileLayout({
  content,
  params,
}: ProfileLayoutProps) {
  const { handle } = await params;

  const t = await getTranslations('pages.profile');

  const tabs: {
    id: string;
    title: string;
    href: string;
  }[] = [
    {
      id: 'resume',
      title: t('resume.label'),
      href: `/@${handle}`,
    },
    {
      id: 'projects',
      title: t('projects.label'),
      href: `/@${handle}/projects`,
    },
    {
      id: 'posts',
      title: t('posts.label'),
      href: `/@${handle}/posts`,
    },
  ];

  return (
    <div className="flex mx-auto max-w-7xl gap-4">
      <div className="mx-auto w-full max-w-3xl">
        <ProfileOverview handle={handle} />

        <ProfileTabs tabs={tabs}>{content}</ProfileTabs>
      </div>
    </div>
  );
}
