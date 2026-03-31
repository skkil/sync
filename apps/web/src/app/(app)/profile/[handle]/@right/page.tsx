import { BriefcaseIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

export default async function ProfileRightSidebar() {
  const t = await getTranslations('pages.profile');

  return (
    <Card>
      <CardContent>
        <Link href="/profile/me/applications">
          <Button variant="ghost" className="w-full">
            <BriefcaseIcon />
            {t('applications.label')}
          </Button>
        </Link>
      </CardContent>
    </Card>
  );
}
