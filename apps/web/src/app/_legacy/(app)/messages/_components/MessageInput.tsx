import { PaperPlaneTiltIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { useState } from 'react';

import { useWebSocket } from '@/components/providers/WebSocketProvider';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

interface MessageInputProps {
  to: string;
}

export default function MessageInput({ to }: MessageInputProps) {
  const t = useTranslations('pages.messages');
  const [message, setMessage] = useState('');

  const { publish } = useWebSocket();

  const handleSendMessage = (content: string) => {
    if (!content.trim()) {
      return;
    }

    publish(
      '/app/conversations/send',
      JSON.stringify({
        to,
        content,
      }),
    );
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
