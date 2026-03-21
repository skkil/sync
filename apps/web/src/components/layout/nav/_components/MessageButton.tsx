'use client';

import { ChatCircleDotsIcon } from '@phosphor-icons/react';
import Link from 'next/link';

import { Button } from '@/components/ui/button';
import { useGetUnreadMessagesCountQuery } from '@/features/message/api/get-unread-messages-count';

export function MessagesButton() {
  const { data: unreadMessagesCount } = useGetUnreadMessagesCountQuery();

  return (
    <Link href="/messages" className="relative">
      <Button variant="ghost" size="icon-lg">
        <ChatCircleDotsIcon />
        {unreadMessagesCount?.some((conv) => conv.unreadCount > 0) && (
          <span className="absolute top-1 right-1 inline-flex h-2 w-2 items-center justify-center rounded-full bg-primary" />
        )}
      </Button>
    </Link>
  );
}
