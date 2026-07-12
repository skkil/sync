import { useTranslations } from 'next-intl';

import { Badge } from '@/components/ui/badge';

import { PostType } from '../../types/post';

export function PostTypeBadge({ type }: { type: PostType }) {
  const t = useTranslations('components.post.type');

  switch (type) {
    case PostType.LONG:
      return <Badge color="long">{t(type)}</Badge>;
    case PostType.QUESTION:
      return <Badge color="question">{t(type)}</Badge>;
    case PostType.SHORT:
      return <Badge color="short">{t(type)}</Badge>;
  }
}
