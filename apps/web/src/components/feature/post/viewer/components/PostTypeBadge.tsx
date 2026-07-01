import { useTranslations } from 'next-intl';

import { Badge } from '@/components/ui/badge';

import { PostType } from '../../types/post';

export function PostTypeBadge({ type }: { type: PostType }) {
  const t = useTranslations('components.post.type');

  switch (type) {
    case PostType.LONG:
      return <Badge color="blue">{t(type)}</Badge>;
    case PostType.QUESTION:
      return <Badge color="amber">{t(type)}</Badge>;
    case PostType.SHORT:
      return <Badge variant="secondary">{t(type)}</Badge>;
  }
}
