import { redirect } from 'next/navigation';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useGetConversations } from '@/features/message/api/get-conversations';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

interface ConversationListProps {
  selected: string | null;
}

export default function ConversationList({ selected }: ConversationListProps) {
  const { data: session } = useSession();
  const { data: conversations } = useGetConversations();

  if (!session?.user || !conversations) {
    return null;
  }

  return (
    <div className="flex flex-col">
      {conversations.map((conversation) => {
        const other = conversation.participants
          .filter((p) => p.id !== session.user.id)
          .at(0);

        if (!other) {
          return null;
        }

        return (
          <div
            key={conversation.id}
            className={cn(
              'hover:bg-muted cursor-pointer px-4 py-2 rounded-md',
              selected === other.id ? 'bg-muted' : '',
            )}
            onClick={() => {
              redirect(`/messages?to=${other.id}`);
            }}
          >
            <div className="flex items-center justify-between gap-2">
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <Avatar className="w-10 h-10">
                    <AvatarFallback></AvatarFallback>
                  </Avatar>

                  <div className="flex-1">
                    <div className="flex items-center justify-between">
                      <h3 className="font-semibold">{other.name}</h3>
                    </div>
                    <p>
                      {conversation.lastMessage &&
                        conversation.lastMessage.content}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}
