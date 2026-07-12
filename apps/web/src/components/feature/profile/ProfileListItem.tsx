import Link from 'next/link';

import { FollowButton } from '@/components/feature/profile/FollowButton';
import { ProfileHoverCard } from '@/components/feature/profile/ProfileHoverCard';
import ROUTES from '@/util/routes';

interface ProfileListItemProps {
  handle: string;
  name: string;
  imageUrl?: string | null;
}

export function ProfileListItem({
  handle,
  name,
  imageUrl,
}: ProfileListItemProps) {
  return (
    <li className="flex items-center gap-3 rounded-lg px-2 py-2 transition-colors hover:bg-muted/60">
      <ProfileHoverCard
        handle={handle}
        name={name}
        imageUrl={imageUrl ?? undefined}
      />

      <Link href={ROUTES.PROFILE(handle)} className="min-w-0 flex-1">
        <p className="truncate text-sm font-medium">{name}</p>
        <p className="truncate text-xs text-muted-foreground">@{handle}</p>
      </Link>

      <FollowButton handle={handle} size="sm" />
    </li>
  );
}
