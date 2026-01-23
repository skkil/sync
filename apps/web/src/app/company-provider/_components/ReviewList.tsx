'use client';

import { useState } from 'react';

import { Button } from '@/components/ui/button';

type Review = {
  id: string;
  author: string;
  role: string;
  rating: string;
  content: string;
};

type ReviewListProps = {
  reviews: Review[];
  moreLabel: string;
  collapseLabel: string;
  initialCount?: number;
};

export default function ReviewList({
  reviews,
  moreLabel,
  collapseLabel,
  initialCount = 2,
}: ReviewListProps) {
  const [expanded, setExpanded] = useState(false);
  const visibleReviews = expanded ? reviews : reviews.slice(0, initialCount);

  if (!reviews.length) {
    return null;
  }

  return (
    <div className="space-y-4">
      {visibleReviews.map((review) => (
        <div
          key={review.id}
          className="rounded-2xl border bg-muted/30 px-5 py-4"
        >
          <div className="flex items-center justify-between">
            <div>
              <div className="text-sm font-semibold">{review.author}</div>
              <div className="text-xs text-muted-foreground">
                {review.role}
              </div>
            </div>
            <div className="text-sm font-semibold">{review.rating}</div>
          </div>
          <p className="mt-3 text-sm text-muted-foreground">
            {review.content}
          </p>
        </div>
      ))}

      {reviews.length > initialCount && (
        <Button
          variant="outline"
          className="w-full"
          onClick={() => setExpanded((prev) => !prev)}
        >
          {expanded ? collapseLabel : moreLabel}
        </Button>
      )}
    </div>
  );
}
