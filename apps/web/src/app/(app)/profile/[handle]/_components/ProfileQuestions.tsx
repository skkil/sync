'use client';

import { useTranslations } from 'next-intl';

import { useGetUserPostsInfinite } from '@/api/__generated__/post/post';
import { useGetProfileByHandle } from '@/api/__generated__/profile/profile';
import { PostType } from '@/components/feature/post/types/post';

import PostFeedList from './PostFeedList';

const QUESTIONS_PAGE_SIZE = '10';

interface ProfileQuestionsProps {
  handle: string;
}

export default function ProfileQuestions({ handle }: ProfileQuestionsProps) {
  const t = useTranslations('pages.profile.tabs.questions');

  const {
    data: profile,
    isPending: isProfilePending,
    isError: isProfileError,
  } = useGetProfileByHandle(handle);

  const userId = profile?.data.userId;

  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isPending: isQuestionsPending,
    isError: isQuestionsError,
  } = useGetUserPostsInfinite(
    userId ?? '',
    {
      first: QUESTIONS_PAGE_SIZE,
      after: '',
      type: PostType.QUESTION,
    },
    {
      query: {
        enabled: !!userId,
        getNextPageParam: (lastPage) => {
          const pageInfo = lastPage.data.posts?.pageInfo;
          return pageInfo?.hasNextPage
            ? (pageInfo.endCursor ?? undefined)
            : undefined;
        },
      },
    },
  );

  const posts =
    data?.pages.flatMap((page) => page.data.posts?.nodes ?? []) ?? [];

  const isPending = isProfilePending || (!!userId && isQuestionsPending);
  const isError = isProfileError || isQuestionsError;

  return (
    <PostFeedList
      posts={posts}
      isPending={isPending}
      isError={isError}
      hasNextPage={hasNextPage}
      isFetchingNextPage={isFetchingNextPage}
      fetchNextPage={fetchNextPage}
      messages={{
        empty: t('empty'),
        error: t('error'),
        end: t('end'),
      }}
    />
  );
}
