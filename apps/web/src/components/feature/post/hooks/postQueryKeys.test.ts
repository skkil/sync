import { describe, expect, it } from 'vitest';

import { isPostRecommendationQueryKey } from './postQueryKeys';

describe('게시글 추천 쿼리 키 판별', () => {
  it.each([
    [['/posts/recommendations']],
    [['/posts/recommendations', { type: 'FOLLOWING' }]],
    [['infinite', '/posts/recommendations']],
    [
      [
        'infinite',
        '/posts/recommendations',
        { type: 'FOLLOWING', postType: 'QUESTION' },
      ],
    ],
  ])('일반 및 무한 추천 쿼리를 판별한다', (queryKey) => {
    expect(isPostRecommendationQueryKey(queryKey)).toBe(true);
  });

  it.each([
    [['/posts']],
    [['infinite', '/posts']],
    [['/users/recommendations']],
  ])('추천 게시글 이외의 쿼리는 제외한다', (queryKey) => {
    expect(isPostRecommendationQueryKey(queryKey)).toBe(false);
  });
});
