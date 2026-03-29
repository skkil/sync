import { BookmarkSimpleIcon } from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';

import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

export default async function JobsRightSidebar() {
  const t = await getTranslations('pages.jobs');

  return (
    <Card>
      <CardContent>
        <Button variant="ghost" className="w-full justify-start">
          <BookmarkSimpleIcon />
          {t('bookmark.label')}
        </Button>
      </CardContent>
    </Card>
  );
}
