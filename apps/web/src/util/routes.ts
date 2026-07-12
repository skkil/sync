const ROUTES = {
  HOME: () => '/',
  ABOUT: () => '/about',
  TERMS: () => '/terms',
  PRIVACY: () => '/privacy',
  EXPLORE_TRENDING: () => '/explore/trending',
  EXPLORE_PROJECTS: () => '/explore/projects',
  EXPLORE_TAGS: () => '/explore/tags',
  DRAFTS: () => '/posts/drafts',
  LOGIN: () => '/auth/login',
  REGISTER: () => '/auth/register',
  ONBOARDING: () => '/onboarding',
  POST: (slug: string) => `/posts/${slug}`,
  TAG: (name: string) => `/tags/${encodeURIComponent(name)}`,
  NEW_POST: () => `/posts/new`,
  PROJECT: (handle: string) => `/projects/${handle}`,
  NEW_PROJECT: () => '/projects/new',
  PROJECT_POSTS: (
    handle: string,
    params?: { type?: string; authorHandle?: string; tagId?: string },
  ) => {
    const base = ROUTES.PROJECT(handle) + '/posts';
    if (!params) return base;

    const searchParams = new URLSearchParams();
    if (params.type) searchParams.set('type', params.type);
    if (params.authorHandle) {
      searchParams.set('authorHandle', params.authorHandle);
    }
    if (params.tagId) searchParams.set('tagId', params.tagId);

    const query = searchParams.toString();
    return query ? `${base}?${query}` : base;
  },
  PROJECT_FEED: (handle: string) => ROUTES.PROJECT_POSTS(handle),
  PROJECT_QUESTIONS: (handle: string) =>
    ROUTES.PROJECT_POSTS(handle, { type: 'QUESTION' }),
  PROJECT_GUIDES: (handle: string) =>
    ROUTES.PROJECT_POSTS(handle, { type: 'LONG' }),
  PROJECT_MY_POSTS: (handle: string) => ROUTES.PROJECT(handle) + '/posts/my',
  PROJECT_MY_COMMENTS: (handle: string) =>
    ROUTES.PROJECT(handle) + '/posts/my-comments',
  PROJECT_TAG_POSTS: (handle: string, tagId: string) =>
    ROUTES.PROJECT_POSTS(handle, { tagId }),
  PROJECT_TAGS: (handle: string) => ROUTES.PROJECT(handle) + '/tags',
  PROJECT_TAGS_MANAGE: (handle: string) =>
    ROUTES.PROJECT_TAGS(handle) + '/manage',
  PROJECT_POST: (projectHandle: string, postHandle: string) =>
    ROUTES.PROJECT(projectHandle) + `/posts/${postHandle}`,
  NEW_PROJECT_POST: (handle: string) => ROUTES.PROJECT(handle) + '/posts/new',
  PROJECT_SETTINGS: (handle: string) => ROUTES.PROJECT(handle) + '/settings',
  PROJECT_SETTINGS_TEAMMATES: (handle: string) =>
    ROUTES.PROJECT(handle) + '/settings/teammates',
  PROJECTS: () => '/projects',
  PROJECT_INVITATIONS: () => '/projects/invitations',
  BOOKMARKS: () => '/bookmarks',
  COOKIES: () => '/cookies',
  PROFILE: (handle: string) => `/@${handle}`,
  PROFILE_FOLLOWERS: (handle: string) => ROUTES.PROFILE(handle) + '/followers',
  PROFILE_FOLLOWING: (handle: string) => ROUTES.PROFILE(handle) + '/following',
  SEARCH: (query?: string, projectHandle?: string) => {
    if (!query) return '/search';
    const params = new URLSearchParams({ q: query });
    if (projectHandle) params.set('projectHandle', projectHandle);
    return `/search?${params.toString()}`;
  },
  MESSAGES: (to?: string) => (to ? `/messages?to=${to}` : '/messages'),
  ADMIN: () => '/admin',
  ADMIN_POST_REPORTS: () => '/admin/post-reports',
};

export default ROUTES;
