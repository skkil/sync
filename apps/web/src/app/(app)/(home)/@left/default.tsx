import {
  BriefcaseIcon,
  LightbulbIcon,
  UsersThreeIcon,
} from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import Footer from '@/components/layout/footer/Footer';
import { Separator } from '@/components/ui/separator';

export default async function HomeLeftSidebar() {
  const t = await getTranslations('pages.home.left');

  return (
    <div className="flex flex-col gap-6 mt-6">
      <div className="flex flex-col gap-4">
        <Link href="/trending-projects">
          <div className="flex w-full items-center gap-2 mx-2">
            <LightbulbIcon />
            {t('trending-projects')}
          </div>
        </Link>

        <Link href="/jobs">
          <div className="flex w-full items-center gap-2 mx-2">
            <BriefcaseIcon />
            {t('jobs')}
          </div>
        </Link>

        <Link href="/teams">
          <div className="flex w-full items-center gap-2 mx-2">
            <UsersThreeIcon />
            {t('team-building')}
          </div>
        </Link>
      </div>

      <Separator />

      <Footer />
    </div>
  );
}
