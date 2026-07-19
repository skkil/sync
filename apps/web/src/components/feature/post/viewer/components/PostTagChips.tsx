import Link from 'next/link';

import { TagBadge } from '@/components/feature/tag/TagBadge';
import ROUTES from '@/util/routes';

import type { PostTagSummary } from '../types';

export function PostTagChips({ tags }: { tags: PostTagSummary[] }) {
  if (tags.length === 0) {
    return null;
  }

  return (
    <div className="flex flex-wrap gap-1.5">
      {tags.map((tag) => (
        <Link
          key={tag.id}
          href={
            tag.projectHandle
              ? ROUTES.PROJECT_TAG(tag.projectHandle, String(tag.id))
              : ROUTES.TAG(String(tag.id))
          }
          onClick={(event) => event.stopPropagation()}
        >
          <TagBadge
            name={tag.name}
            isProjectTag={!!tag.projectHandle}
            variant="secondary"
          />
        </Link>
      ))}
    </div>
  );
}
