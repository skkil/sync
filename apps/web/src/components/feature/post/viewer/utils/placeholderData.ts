// TODO: there's no verification/review feature yet — this derives a fake
// review status from the post id purely so the design's review states have
// something to render. Replace with the real field once it exists.
export type ReviewStatus = 'verified' | 'verify-soon' | 'none';

const REVIEW_STATUSES: ReviewStatus[] = ['verified', 'verify-soon', 'none'];

export function getMockReviewStatus(id: number): ReviewStatus {
  return REVIEW_STATUSES[id % REVIEW_STATUSES.length] ?? 'none';
}

// TODO: posts have no tags yet — placeholder pool used until tagging on
// posts is implemented.
const MOCK_TAG_POOL = ['edge', 'performance', 'redis', 'infra', 'auth'];

export function getMockTags(id: number): string[] {
  const start = id % MOCK_TAG_POOL.length;
  const end = (start + 1) % MOCK_TAG_POOL.length;
  return [MOCK_TAG_POOL[start]!, MOCK_TAG_POOL[end]!];
}
