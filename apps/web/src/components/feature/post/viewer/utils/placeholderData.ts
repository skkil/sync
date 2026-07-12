// TODO: there's no verification/review feature yet — this derives a fake
// review status from the post id purely so the design's review states have
// something to render. Replace with the real field once it exists.
export type ReviewStatus = 'verified' | 'verify-soon' | 'none';

const REVIEW_STATUSES: ReviewStatus[] = ['verified', 'verify-soon', 'none'];

export function getMockReviewStatus(id: number): ReviewStatus {
  return REVIEW_STATUSES[id % REVIEW_STATUSES.length] ?? 'none';
}
