import { PaperPlaneTiltIcon } from '@phosphor-icons/react';
import { Client } from '@stomp/stompjs';
import { useTranslations } from 'next-intl';
import { useEffect, useRef, useState } from 'react';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { getStompClient } from '@/lib/ws';

interface MessageInputProps {
  to: string;
}

export default function MessageInput({ to }: MessageInputProps) {
  const t = useTranslations('pages.messages');
  const [message, setMessage] = useState('');

  const client = useRef<Client | null>(null);

  useEffect(() => {
    if (!client.current) {
      client.current = getStompClient();
    }

    client.current.activate();

    return () => {
      if (client.current) {
        client.current.deactivate();
      }
    };
  }, [client]);

  const handleSendMessage = (content: string) => {
    if (!content.trim()) {
      return;
    }

    client.current?.publish({
      destination: '/app/conversations/send',
      body: JSON.stringify({
        to,
        content,
      }),
    });
  };

  return (
    <div className="flex items-center gap-2">
      <Input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        placeholder={t('main.input.placeholder')}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            handleSendMessage(message);
            setMessage('');
          }
        }}
      />
      <Button
        onClick={() => {
          handleSendMessage(message);
          setMessage('');
        }}
        size="icon"
        disabled={!message.trim()}
      >
        <PaperPlaneTiltIcon />
      </Button>
    </div>
  );
}
