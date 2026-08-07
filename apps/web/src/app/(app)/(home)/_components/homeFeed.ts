import type { GetPostRecommendationsParams } from '@/api/__generated__/types/GetPostRecommendationsParams';
import { PostType } from '@/components/feature/post/types/post';

export const HOME_FEED_FILTERS = [
  { value: 'all', postType: undefined },
  { value: 'shorts', postType: PostType.SHORT },
  { value: 'articles', postType: PostType.LONG },
  { value: 'questions', postType: PostType.QUESTION },
] as const;

export type HomeFeedFilter = (typeof HOME_FEED_FILTERS)[number]['value'];

export function createHomeFeedParams(
  filter: HomeFeedFilter,
  pageSize: string,
): GetPostRecommendationsParams {
  const postType = HOME_FEED_FILTERS.find(
    (item) => item.value === filter,
  )?.postType;

  return {
    type: 'FOLLOWING',
    first: pageSize,
    after: '',
    ...(postType ? { postType } : {}),
  };
}
