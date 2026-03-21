import { IMessage } from '@stomp/stompjs';

export enum WebSocketEventType {
  INCOMING_MESSAGE,
}

interface IncomingMessageEventPayload {
  conversationId: string;
  messageId: string;
  content: string;
  sender: string;
}

export interface WebSocketEventPayloadMap {
  [WebSocketEventType.INCOMING_MESSAGE]: IncomingMessageEventPayload;
}

export interface WebSocketEvent<T extends WebSocketEventType> {
  type: T;
  timestamp: number;
  payload: WebSocketEventPayloadMap[T];
}

export interface WebSocketEventHandler<T extends WebSocketEventType> {
  type: T;

  getSubscriptionDestination(userId: string): string;

  parseMessage(message: IMessage): WebSocketEvent<T>;

  handleEvent(event: WebSocketEvent<T>): void;
}
