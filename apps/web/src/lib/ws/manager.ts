import { Client, StompSubscription } from '@stomp/stompjs';

import { WebSocketEventHandler, WebSocketEventType } from './events';

type WebSocketEventHandlerMap = {
  [T in WebSocketEventType]?: WebSocketEventHandler<T>;
};

export class WebSocketEventManager {
  private client: Client;
  private handlers: WebSocketEventHandlerMap;
  private subscriptions: Map<string, StompSubscription>;

  constructor(client: Client) {
    this.client = client;
    this.handlers = {};
    this.subscriptions = new Map();
  }

  registerHandler<T extends WebSocketEventType>(
    handler: WebSocketEventHandler<T>,
  ) {
    this.handlers[handler.type] = handler;
  }

  subscribe(userId: string) {
    console.log('Subscribing to WebSocket events for user:', userId);
    if (!this.client.connected) {
      console.warn(
        'WebSocket client is not connected. Cannot subscribe to events.',
      );
      return;
    }

    Object.entries(this.handlers).forEach(([, handler]) => {
      const destination = handler.getSubscriptionDestination(userId);

      const subscription = this.client.subscribe(destination, (message) => {
        try {
          const event = handler.parseMessage(message);
          handler.handleEvent(event);
        } catch (error) {
          console.error('Failed to handle WebSocket message:', error);
        }
      });

      if (this.subscriptions.has(destination)) {
        this.subscriptions.get(destination)?.unsubscribe();
      }

      this.subscriptions.set(destination, subscription);
    });
  }

  unsubscribeAll() {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
    this.subscriptions.clear();
  }
}
