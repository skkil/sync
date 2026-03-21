'use client';

import { SidebarIcon } from '@phosphor-icons/react';
import { useWindowSize } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import { redirect, useSearchParams } from 'next/navigation';
import { useEffect, useEffectEvent, useState } from 'react';

import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { useCreateConversationMutation } from '@/features/message/api/create-conversation';
import { useSession } from '@/lib/auth/client';
import { cn } from '@/lib/utils';

import ConversationList from './_components/ConversationList';
import MessageInput from './_components/MessageInput';
import MessagesList from './_components/MessagesList';
import SelectedProfile from './_components/SelectedProfile';

export default function Messages() {
  const t = useTranslations('pages.messages');

  const { data: session, isPending } = useSession();

  const [selectedConversation, setSelectedConversation] = useState<
    string | null
  >(null);

  const searchParams = useSearchParams();
  const to = searchParams.get('to');

  const { mutate: createConversation } = useCreateConversationMutation();

  useEffect(() => {
    if (to && session) {
      createConversation(to, {
        onSuccess: ({ conversationId }) => {
          setSelectedConversation(conversationId);
        },
      });
    }
  }, [to, session, createConversation]);

  const [sidebarOpen, setSidebarOpen] = useState(true);

  const { width } = useWindowSize();

  useEffectEvent(() => {
    if (width) {
      setSidebarOpen(width >= 768);
    }
  });

  if (isPending) {
    return null;
  }

  if (!session) {
    redirect('/auth/login');
  }

  return (
    <div className="flex flex-col items-center gap-4 h-full bg-background p-4">
      <Card className="grow w-full h-full flex p-0">
        <CardContent className="flex-1 flex overflow-hidden p-0 relative">
          {sidebarOpen && (
            <div
              className="absolute inset-0 z-10 md:hidden"
              onClick={() => setSidebarOpen(false)}
            />
          )}

          <div
            className={cn(
              'border-r transition-all duration-300 flex flex-col',
              sidebarOpen
                ? 'w-full md:w-80 absolute md:relative top-0 left-0 bottom-0 z-20 md:z-auto bg-background'
                : 'w-0',
            )}
          >
            {sidebarOpen && (
              <div className="flex-1 flex flex-col overflow-hidden gap-2">
                <div className="px-4 pt-4">
                  <div className="flex items-center justify-between">
                    <h2 className="text-xl font-semibold flex items-center gap-2">
                      {t('conversations.title')}
                    </h2>

                    <Button
                      onClick={() => setSidebarOpen(false)}
                      variant="ghost"
                    >
                      <SidebarIcon />
                    </Button>
                  </div>
                </div>

                <div className="px-4">
                  <Input type="text" placeholder={t('conversations.search')} />
                </div>

                <div className="flex-1 overflow-y-auto p-4">
                  <ConversationList
                    selectedConversationId={selectedConversation}
                    onSelectConversation={(conversationId) => {
                      setSelectedConversation(conversationId);
                    }}
                  />
                </div>
              </div>
            )}
          </div>

          <div className="grow flex flex-col">
            {selectedConversation && (
              <div className="border-b p-4">
                <div className="flex items-center justify-between gap-3">
                  <div className="flex items-center gap-2">
                    {!sidebarOpen && (
                      <Button
                        onClick={() => setSidebarOpen(true)}
                        variant="ghost"
                      >
                        <SidebarIcon />
                      </Button>
                    )}

                    {to && <SelectedProfile to={to} />}
                  </div>
                </div>
              </div>
            )}

            <div className="grow overflow-y-auto p-4 space-y-4">
              {selectedConversation && (
                <MessagesList conversationId={selectedConversation} />
              )}
            </div>

            {selectedConversation && (
              <div className="border-t p-4">
                <MessageInput conversationId={selectedConversation} />
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
