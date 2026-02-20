import { useMutation } from '@tanstack/react-query';

import { server } from '@/lib/server';
import { ProviderType } from '@/types/provider';

type CreateReviewRequest = {
  review: string;
} & (
  | {
      type: ProviderType.SCHOOL;
      academicQuality: number;
      campusFacilities: number;
      studentLife: number;
      valueForMoney: number;
    }
  | {
      type: ProviderType.LAB;
      professorPersonality: number;
      labAtmosphere: number;
      workLifeBalance: number;
      compensation: number;
    }
);

async function createReview(providerId: string, request: CreateReviewRequest) {
  return server.post(`providers/${providerId}/reviews`, {
    json: request,
  });
}

export const useCreateReviewMutation = (providerId: string) => {
  return useMutation({
    mutationFn: (request: CreateReviewRequest) =>
      createReview(providerId, request),
  });
};
