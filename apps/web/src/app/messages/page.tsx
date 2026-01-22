'use client';

import {
  CalendarIcon,
  CaretRightIcon,
  CoffeeIcon,
  InfoIcon,
  PaperPlaneTiltIcon,
  PlusIcon,
  SidebarIcon,
  UserIcon,
  UserMinusIcon,
  UserPlusIcon,
} from '@phosphor-icons/react';
import { useWindowSize } from '@uidotdev/usehooks';
import { useTranslations } from 'next-intl';
import Link from 'next/link';
import { useEffect, useState } from 'react';

import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

const conversations = [
  {
    id: 1,
    name: 'User',
    lastMessage: 'Hey, how are you?',
    unread: 2,
  },
];

const messages = [
  {
    id: 1,
    sender: 'User',
    text: 'Hey, how are you?',
    time: '10:30 AM',
    isOwn: false,
  },
  {
    id: 2,
    text: "I'm good! How about you?",
    time: '10:32 AM',
    isOwn: true,
  },
  {
    id: 3,
    sender: 'User',
    text: 'Doing great! Want to grab lunch?',
    time: '10:35 AM',
    isOwn: false,
  },
];

export default function Messages() {
  const t = useTranslations('pages.messages');

  const { width } = useWindowSize();

  const [leftSidebarOpen, setLeftSidebarOpen] = useState(true);
  const [rightSidebarOpen, setRightSidebarOpen] = useState(true);
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!width) {
      return;
    }

    if (width < 768) {
      setLeftSidebarOpen(false);
      setRightSidebarOpen(false);
    } else if (width < 1024) {
      setLeftSidebarOpen(true);
      setRightSidebarOpen(false);
    } else {
      setLeftSidebarOpen(true);
      setRightSidebarOpen(true);
    }
  }, [width]);

  function LeftSidebar() {
    return (
      <div
        className={cn(
          'border-r transition-all duration-300 flex flex-col',
          leftSidebarOpen
            ? 'w-full md:w-80 absolute md:relative top-0 left-0 bottom-0 z-20 md:z-auto'
            : 'w-0',
        )}
      >
        {leftSidebarOpen && (
          <div className="flex-1 flex flex-col overflow-hidden gap-2">
            <div className="px-4 pt-4">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold flex items-center gap-2">
                  {t('conversations.title')}
                </h2>

                <Button
                  onClick={() => setLeftSidebarOpen(false)}
                  variant="ghost"
                >
                  <SidebarIcon />
                </Button>
              </div>
            </div>

            <div className="px-4">
              <Input type="text" placeholder={t('conversations.search')} />
            </div>

            <div className="flex-1 overflow-y-auto">
              {conversations.map((conversation) => (
                <div
                  key={conversation.id}
                  className="p-4 hover:bg-muted cursor-pointer border-b"
                >
                  <div className="flex items-center justify-between gap-2">
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <Avatar className="w-10 h-10">
                          <AvatarFallback></AvatarFallback>
                        </Avatar>

                        <div className="flex-1">
                          <div className="flex items-center justify-between">
                            <h3 className="font-semibold">
                              {conversation.name}
                            </h3>
                          </div>
                          <p className="text-xs">{conversation.lastMessage}</p>
                        </div>
                      </div>
                    </div>

                    {conversation.unread > 0 && (
                      <Badge>{conversation.unread}</Badge>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  }

  function Main() {
    return (
      <div className="flex-1 flex flex-col">
        <div className="border-b p-4">
          <div className="flex items-center justify-between gap-3">
            <div className="flex items-center gap-2">
              {!leftSidebarOpen && (
                <Button
                  onClick={() => setLeftSidebarOpen(true)}
                  variant="ghost"
                >
                  <SidebarIcon />
                </Button>
              )}

              <Avatar className="w-10 h-10">
                <AvatarFallback></AvatarFallback>
              </Avatar>

              <div>
                <h3 className="font-semibold">User</h3>
                <p className="text-xs font-light">CEO at Samsung</p>
              </div>
            </div>

            {!rightSidebarOpen && (
              <Button variant="ghost" onClick={() => setRightSidebarOpen(true)}>
                <InfoIcon />
              </Button>
            )}
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex ${msg.isOwn ? 'justify-end' : 'justify-start'}`}
            >
              <div className={`max-w-md ${msg.isOwn ? 'order-2' : 'order-1'}`}>
                {!msg.isOwn && <p className="text-xs mb-1">{msg.sender}</p>}
                <div
                  className={`px-4 py-2 rounded-2xl ${
                    msg.isOwn
                      ? 'bg-muted rounded-br-sm'
                      : 'bg-primary text-white rounded-bl-sm'
                  }`}
                >
                  <p>{msg.text}</p>
                </div>
                <p className="text-xs mt-1">{msg.time}</p>
              </div>
            </div>
          ))}
        </div>

        <div className="border-t p-4">
          <div className="mb-2 flex gap-2">
            <Badge className="bg-brown-100 text-brown-800">
              <CoffeeIcon />
              {t('main.input.helpers.coffee-chat')}
            </Badge>

            <Badge className="bg-green-100 text-green-800">
              <UserPlusIcon />
              {t('main.input.helpers.invite-to-project')}
            </Badge>

            <Badge className="bg-blue-100 text-blue-800">
              <CalendarIcon />
              {t('main.input.helpers.schedule-meeting')}
            </Badge>

            <Badge>
              <PlusIcon />
            </Badge>
          </div>

          <div className="flex items-center gap-2">
            <Input
              type="text"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder={t('main.input.placeholder')}
              className="flex-1 px-4 py-3 border rounded-full focus:outline-none focus:ring-2"
              onKeyDown={(e) => {
                if (e.key === 'Enter' && message.trim()) {
                  setMessage('');
                }
              }}
            />
            <Button
              onClick={() => {
                if (message.trim()) {
                  setMessage('');
                }
              }}
              size="icon"
              disabled={!message.trim()}
            >
              <PaperPlaneTiltIcon />
            </Button>
          </div>
        </div>
      </div>
    );
  }

  function RightSidebar() {
    return (
      <div
        className={cn(
          'border-l transition-all duration-300 flex flex-col',
          rightSidebarOpen
            ? 'w-full md:w-80 absolute md:relative top-0 right-0 bottom-0 z-20 md:z-auto'
            : 'w-0',
        )}
      >
        {rightSidebarOpen && (
          <div className="flex-1 flex flex-col overflow-hidden">
            <div className="p-4">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold flex items-center gap-2">
                  <InfoIcon />
                  {t('details.title')}
                </h2>
                <Button
                  onClick={() => setRightSidebarOpen(false)}
                  variant="ghost"
                >
                  <CaretRightIcon />
                </Button>
              </div>
            </div>

            <div className="p-6 text-center border-b">
              <Avatar className="w-20 h-20 mx-auto">
                <AvatarFallback></AvatarFallback>
              </Avatar>
              <h3 className="font-semibold text-lg">User</h3>

              <div>
                <Link href="/profile/1">
                  <Button className="w-full">
                    <UserIcon />
                    {t('details.visit-profile')}
                  </Button>
                </Link>
              </div>
            </div>

            <div className="flex-1 overflow-y-auto">
              <div className="p-4 flex flex-col gap-2">
                <div className="flex flex-col">
                  <h4 className="font-semibold mb-2">
                    {t('details.schedule.title')}
                  </h4>

                  <Calendar className="self-center" />
                </div>
                <h4 className="font-semibold mb-2">
                  {t('details.memo.title')}
                </h4>
                <div className="space-y-2">
                  <ul>
                    <li>
                      Met at a tech conference on <strong>March 1, 2024</strong>
                      .
                    </li>
                    <li>
                      Connected on LinkedIn on <strong>March 5, 2024</strong>.
                    </li>
                  </ul>
                </div>
              </div>

              <div className="p-4">
                <h4 className="font-semibold mb-2">
                  {t('details.settings.title')}
                </h4>
                <div className="space-y-2">
                  <Button variant="destructive" className="w-full">
                    <UserMinusIcon />
                    {t('details.settings.block')}
                  </Button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center gap-4 min-h-screen bg-background p-4">
      <nav className="flex w-full items-center justify-between border-b p-3 px-8">
        sync
      </nav>

      <Card className="grow w-full h-full flex p-0">
        <CardContent className="flex-1 flex overflow-hidden p-0 relative">
          {leftSidebarOpen && (
            <div
              className="absolute inset-0 z-10 md:hidden"
              onClick={() => setLeftSidebarOpen(false)}
            />
          )}

          {rightSidebarOpen && (
            <div
              className="absolute inset-0 z-10 md:hidden"
              onClick={() => setRightSidebarOpen(false)}
            />
          )}
          <LeftSidebar />
          <Main />
          <RightSidebar />
        </CardContent>
      </Card>
    </div>
  );
}
