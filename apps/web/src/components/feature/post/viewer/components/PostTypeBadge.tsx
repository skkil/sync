import { useTranslations } from 'next-intl';

import { Badge } from '@/components/ui/badge';
import { PostType } from '@/features/post/constants/post-type';

export function PostTypeBadge({ type }: { type: PostType }) {
  const t = useTranslations('components.post.type');

  switch (type) {
    case PostType.Long:
      return <Badge color="blue">{t(type)}</Badge>;
    case PostType.Question:
      return <Badge color="amber">{t(type)}</Badge>;
    case PostType.Short:
      return <Badge variant="secondary">{t(type)}</Badge>;
  }
}
