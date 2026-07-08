export interface PostAuthorSummary {
  name: string;
  handle: string;
  profileImageUrl?: string | null;
}

export interface PostProjectSummary {
  handle?: string | null;
  name?: string | null;
}
