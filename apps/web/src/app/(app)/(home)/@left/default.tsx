import { BriefcaseIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import Footer from '@/components/layout/footer/Footer';
import { Separator } from '@/components/ui/separator';

export default async function HomeLeftSidebar() {
  const t = await getTranslations('pages.home.left');

  return (
    <div className="flex flex-col gap-6 mt-6">
      <div>
        <Link href="/jobs">
          <div className="flex w-full items-center gap-2 mx-2">
            <BriefcaseIcon />
            {t('jobs')}
          </div>
        </Link>
      </div>

      <Separator />

      <Footer />
    </div>
  );
}
