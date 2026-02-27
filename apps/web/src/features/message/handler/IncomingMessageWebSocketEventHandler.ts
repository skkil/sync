import { IMessage } from '@stomp/stompjs';

import { getQueryClient } from '@/lib/query';
import {
  WebSocketEvent,
  WebSocketEventHandler,
  WebSocketEventType,
} from '@/lib/ws/events';
import { Conversation, Message } from '@/types/message';

export interface IncomingMessagePayload {
  conversationId: string;
  messageId: string;
  senderId: string;
  content: string;
  sentAt: string;
}

export class IncomingMessageWebSocketEventHandler implements WebSocketEventHandler<WebSocketEventType.INCOMING_MESSAGE> {
  type = WebSocketEventType.INCOMING_MESSAGE;

  getSubscriptionDestination(): string {
    return `/user/queue/messages`;
  }

  parseMessage(message: IMessage): WebSocketEvent<WebSocketEventType> {
    const body: IncomingMessagePayload = JSON.parse(message.body);

    return {
      type: WebSocketEventType.INCOMING_MESSAGE,
      timestamp: body.sentAt ? new Date(body.sentAt).getTime() : Date.now(),
      payload: {
        conversationId: body.conversationId,
        messageId: body.messageId,
        content: body.content,
        sender: body.senderId,
      },
    };
  }

  handleEvent(event: WebSocketEvent<WebSocketEventType>): void {
    const queryClient = getQueryClient();

    const conversations = queryClient.getQueryData<Conversation[]>([
      'conversations',
    ]);
    if (
      conversations &&
      conversations.find((conv) => conv.id === event.payload.conversationId) ===
        undefined
    ) {
      queryClient.setQueryData(
        ['conversations'],
        (oldConversations: Conversation[]) => {
          const newConversation: Conversation = {
            id: event.payload.conversationId,
            participants: [
              {
                id: event.payload.sender,
                name: '',
              },
            ],
            lastMessage: {
              id: event.payload.messageId,
              senderId: event.payload.sender,
              content: event.payload.content,
              sentAt: new Date(event.timestamp),
            },
          };

          return [...(oldConversations || []), newConversation];
        },
      );
    }

    queryClient.setQueryData(
      ['conversations', event.payload.conversationId, 'messages'],
      (oldMessages: Message[]) => {
        const newMessage = {
          id: event.payload.messageId,
          content: event.payload.content,
          senderId: event.payload.sender,
          sentAt: new Date(event.timestamp),
        };

        return [...(oldMessages || []), newMessage];
      },
    );
  }
}
