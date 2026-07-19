'use client';

import { MagnifyingGlassIcon } from '@phosphor-icons/react';
import { useTranslations } from 'next-intl';
import { forwardRef, useEffect, useImperativeHandle, useState } from 'react';
import { toast } from 'sonner';

import {
  useGetFollowedTags,
  useGetTagRecommendations,
} from '@/api/__generated__/tag/tag';
import { RemovableTagBadge } from '@/components/feature/tag/TagBadge';
import { useFollowTag } from '@/components/feature/tag/hooks/useFollowTag';
import { useTagSearch } from '@/components/feature/tag/hooks/useTagSearch';
import { useUnfollowTag } from '@/components/feature/tag/hooks/useUnfollowTag';
import { Button } from '@/components/ui/button';
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from '@/components/ui/input-group';
import { SeparatorWithText } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { useSession } from '@/lib/auth/client';

import { OnboardingStepContentProps, OnboardingStepContentRef } from '../page';

const MINIMUM_FOLLOWED_TAGS = 3;
const MAXIMUM_FOLLOWED_TAGS = 5;

export const TagFollowStep = forwardRef<
  OnboardingStepContentRef,
  OnboardingStepContentProps
>(({ onStateChange }, ref) => {
  const t = useTranslations('pages.onboarding.steps.tags');

  const { data: session } = useSession();
  const handle = session?.user.handle ?? '';

  const { data: recommendedTagsData, isPending: isRecommendedTagsPending } =
    useGetTagRecommendations();
  const recommendedTags = recommendedTagsData?.data.tags ?? [];

  const {
    query,
    setQuery,
    debouncedQuery,
    tags: searchedTags,
    isPending: isSearchTagsPending,
  } = useTagSearch();

  const isSearching = debouncedQuery.length > 0;
  const tags = isSearching ? searchedTags : recommendedTags;
  const isTagsPending = isSearching
    ? isSearchTagsPending
    : isRecommendedTagsPending;

  const { data: followedTagsData } = useGetFollowedTags(
    handle,
    { page: '0', size: '100' },
    { query: { enabled: !!handle } },
  );

  const { mutate: followTag } = useFollowTag({ handle });
  const { mutate: unfollowTag } = useUnfollowTag({ handle });

  const [selectedTags, setSelectedTags] = useState<
    Map<number, { id: number; name: string }>
  >(new Map());
  const [isSeeded, setIsSeeded] = useState(false);
  const [pendingIds, setPendingIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (isSeeded || !followedTagsData) {
      return;
    }

    setIsSeeded(true);
    setSelectedTags(
      new Map(
        (followedTagsData.data.tags?.content ?? []).map((tag) => [
          tag.id,
          { id: tag.id, name: tag.name },
        ]),
      ),
    );
  }, [isSeeded, followedTagsData]);

  const followedCount = selectedTags.size;

  useImperativeHandle(ref, () => ({
    submit: (onSuccess) => onSuccess(),
  }));

  useEffect(() => {
    onStateChange({
      isPending: false,
      isValid: followedCount >= MINIMUM_FOLLOWED_TAGS,
    });
  }, [onStateChange, followedCount]);

  const setPending = (tagId: number, value: boolean) => {
    setPendingIds((prev) => {
      const next = new Set(prev);
      if (value) {
        next.add(tagId);
      } else {
        next.delete(tagId);
      }
      return next;
    });
  };

  const followTagAction = (tag: { id: number; name: string }) => {
    if (followedCount >= MAXIMUM_FOLLOWED_TAGS) {
      toast.error(t('max-tags', { count: MAXIMUM_FOLLOWED_TAGS }));
      return;
    }

    setPending(tag.id, true);
    setSelectedTags((prev) => new Map(prev).set(tag.id, tag));

    followTag(
      { tagId: String(tag.id) },
      { onSettled: () => setPending(tag.id, false) },
    );
  };

  const unfollowTagAction = (tagId: number) => {
    setPending(tagId, true);
    setSelectedTags((prev) => {
      const next = new Map(prev);
      next.delete(tagId);
      return next;
    });

    unfollowTag(
      { tagId: String(tagId) },
      { onSettled: () => setPending(tagId, false) },
    );
  };

  const suggestedTags = tags.filter((tag) => !selectedTags.has(tag.id));

  return (
    <div className="flex flex-col gap-4">
      <InputGroup>
        <InputGroupAddon>
          <MagnifyingGlassIcon size={18} />
        </InputGroupAddon>
        <InputGroupInput
          value={query}
          onChange={(event) => setQuery(event.target.value)}
          placeholder={t('search.placeholder')}
        />
      </InputGroup>

      <div className="flex flex-wrap gap-2">
        {isTagsPending ? (
          <TagFollowStepSkeleton />
        ) : suggestedTags.length === 0 ? (
          <p className="text-muted-foreground text-sm py-6 text-center w-full">
            {isSearching ? t('search.empty') : t('recommendations.empty')}
          </p>
        ) : (
          suggestedTags.map((tag) => (
            <Button
              key={tag.id}
              type="button"
              size="sm"
              isPending={pendingIds.has(tag.id)}
              disabled={followedCount >= MAXIMUM_FOLLOWED_TAGS}
              onClick={() => followTagAction({ id: tag.id, name: tag.name })}
            >
              {tag.name}
            </Button>
          ))
        )}
      </div>

      <SeparatorWithText>{t('selected.title')}</SeparatorWithText>

      <div className="flex flex-col gap-2">
        <p
          className={
            followedCount >= MINIMUM_FOLLOWED_TAGS
              ? 'text-sm text-success-text'
              : 'text-sm text-muted-foreground'
          }
        >
          {followedCount >= MINIMUM_FOLLOWED_TAGS
            ? t('progress-success')
            : t('progress-below-minimum', { minimum: MINIMUM_FOLLOWED_TAGS })}
        </p>

        {followedCount >= MAXIMUM_FOLLOWED_TAGS && (
          <p className="text-sm text-muted-foreground">
            {t('progress-max-reached', { maximum: MAXIMUM_FOLLOWED_TAGS })}
          </p>
        )}

        <div className="flex flex-wrap gap-2">
          {selectedTags.size === 0 ? (
            <p className="text-muted-foreground text-sm">
              {t('selected.empty')}
            </p>
          ) : (
            Array.from(selectedTags.values()).map((tag) => (
              <RemovableTagBadge
                key={tag.id}
                name={tag.name}
                onRemove={() => unfollowTagAction(tag.id)}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
});
TagFollowStep.displayName = 'TagFollowStep';

function TagFollowStepSkeleton() {
  return (
    <>
      {Array.from({ length: 6 }).map((_, index) => (
        <Skeleton key={index} className="h-8 w-20 rounded-full" />
      ))}
    </>
  );
}
