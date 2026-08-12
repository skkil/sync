'use client';

import { useTranslations } from 'next-intl';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { cn } from '@/lib/utils';

import type { PostTocItem } from '../PostContext';

interface PostTableOfContentsListProps {
  items: PostTocItem[];
}

export function PostTableOfContentsList({
  items,
}: PostTableOfContentsListProps) {
  const t = useTranslations('components.post.viewer.toc');

  if (items.length < 2) {
    return null;
  }

  return (
    <Card className="shrink-0">
      <CardHeader>
        <CardTitle className="text-base">{t('title')}</CardTitle>
      </CardHeader>
      <CardContent>
        <nav aria-label={t('title')}>
          <ul className="flex flex-col gap-0.5">
            {items.map((item) => (
              <li key={item.id}>
                <a
                  href={`#${item.id}`}
                  className={cn(
                    'block truncate rounded-md py-1.5 pr-2 text-sm text-muted-foreground hover:bg-muted hover:text-foreground active:bg-accent',
                    item.isActive && 'bg-accent font-medium text-foreground',
                  )}
                  style={{ paddingLeft: `${(item.level - 1) * 12 + 8}px` }}
                >
                  {item.textContent}
                </a>
              </li>
            ))}
          </ul>
        </nav>
      </CardContent>
    </Card>
  );
}
