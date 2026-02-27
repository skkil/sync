import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { url } from '@/util/server';

interface CreateConversationResponse {
  conversationId: string;
}

async function createConversation(participantId: string) {
  return server
    .post<CreateConversationResponse>(
      url('conversations', {
        participantId,
      }),
    )
    .json();
}

export function useCreateConversationMutation() {
  return useMutation({
    mutationFn: createConversation,
  });
}
