export type Review = {
  id: string;
  author: string;
  role: string;
  rating: string;
  content: string;
};

export type ReviewListProps = {
  reviews: Review[];
  moreLabel: string;
  collapseLabel: string;
  initialCount?: number;
};
