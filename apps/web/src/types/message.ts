export type Conversation = {
  id: string;
  participants: {
    id: string;
    name: string;
  }[];
  lastMessage?: {
    id: string;
    senderId: string;
    content: string;
    sentAt: Date;
  };
};

export type Message = {
  id: string;
  senderId: string;
  content: string;
  sentAt: Date;
};
