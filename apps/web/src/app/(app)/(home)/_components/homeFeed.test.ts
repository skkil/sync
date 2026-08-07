import { describe, expect, it } from 'vitest';

import { PostType } from '@/components/feature/post/types/post';

import { createHomeFeedParams } from './homeFeed';

describe('홈 피드 요청 파라미터', () => {
  it('전체 탭은 게시글 형태를 제한하지 않는다', () => {
    expect(createHomeFeedParams('all', '50')).toEqual({
      type: 'FOLLOWING',
      first: '50',
      after: '',
    });
  });

  it.each([
    ['shorts', PostType.SHORT],
    ['articles', PostType.LONG],
    ['questions', PostType.QUESTION],
  ] as const)('%s 탭은 서버에 게시글 형태를 전달한다', (filter, postType) => {
    expect(createHomeFeedParams(filter, '50')).toMatchObject({ postType });
  });
});
