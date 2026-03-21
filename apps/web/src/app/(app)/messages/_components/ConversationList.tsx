import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useGetConversationsQuery } from '@/features/message/api/get-conversations';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

interface ConversationListProps {
  selectedConversationId: string | null;
  onSelectConversation: (conversationId: string) => void;
}

export default function ConversationList({
  selectedConversationId,
  onSelectConversation,
}: ConversationListProps) {
  const { data: session } = useSession();
  const { data: conversations } = useGetConversationsQuery();

  if (!session?.user || !conversations) {
    return null;
  }

  return (
    <div className="flex flex-col">
      {conversations &&
        conversations.map((conversation) => {
          return (
            <div
              key={conversation.id}
              onClick={() => {
                onSelectConversation(conversation.id);
              }}
              className={cn(
                'cursor-pointer p-2 rounded-md',
                conversation.id === selectedConversationId
                  ? 'bg-primary text-primary-foreground'
                  : 'hover:bg-secondary',
              )}
            >
              <div className="flex items-center justify-between gap-2">
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <Avatar className="w-10 h-10">
                      <AvatarFallback></AvatarFallback>
                    </Avatar>

                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <h3 className="font-semibold">{conversation.id}</h3>
                      </div>
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
