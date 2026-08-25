const ROUTES = {
  HOME: () => '/',
  ABOUT: () => '/about',
  PRICING: () => '/pricing',
  TERMS: () => '/terms',
  PRIVACY: () => '/privacy',
  EXPLORE_TRENDING: () => '/explore/trending',
  EXPLORE_PROJECTS: () => '/explore/projects',
  EXPLORE_TAGS: () => '/explore/tags',
  LOGIN: () => '/auth/login',
  REGISTER: () => '/auth/register',
  FORGOT_PASSWORD: () => '/auth/forgot-password',
  RESET_PASSWORD: (token?: string) =>
    token
      ? `/auth/reset-password?token=${encodeURIComponent(token)}`
      : '/auth/reset-password',
  ONBOARDING: () => '/onboarding',
  POST: (slug: string) => `/posts/${slug}`,
  POST_EDIT: (slug: string) => `/posts/${slug}/edit`,
  POST_OG_IMAGE: (slug: string) => `/og/posts/${encodeURIComponent(slug)}`,
  NEW_POST: () => `/posts/new`,
  PROJECT: (handle: string) => `/projects/${handle}`,
  NEW_PROJECT: () => '/projects/new',
  PROJECT_POSTS: (
    handle: string,
    params?: { type?: string; authorHandle?: string },
  ) => {
    const base = ROUTES.PROJECT(handle) + '/posts';
    if (!params) return base;

    const searchParams = new URLSearchParams();
    if (params.type) searchParams.set('type', params.type);
    if (params.authorHandle) {
      searchParams.set('authorHandle', params.authorHandle);
    }

    const query = searchParams.toString();
    return query ? `${base}?${query}` : base;
  },
  PROJECT_MY_POSTS: (handle: string) => ROUTES.PROJECT(handle) + '/posts/my',
  PROJECT_MY_COMMENTS: (handle: string) =>
    ROUTES.PROJECT(handle) + '/posts/my-comments',
  PROJECT_BOOKMARKS: (handle: string) =>
    ROUTES.PROJECT(handle) + '/posts/bookmarks',
  PROJECT_COLLECTIONS: (handle: string) =>
    ROUTES.PROJECT(handle) + '/collections',
  PROJECT_MEMBERS: (handle: string) => ROUTES.PROJECT(handle) + '/members',
  PROJECT_DRAFTS: (handle: string) => ROUTES.PROJECT(handle) + '/posts/drafts',
  TAG: (id: string) => `/tags/${id}`,
  PROJECT_TAG: (handle: string, id: string) =>
    ROUTES.PROJECT_TAGS(handle) + `/${id}`,
  PROJECT_TAGS: (handle: string) => ROUTES.PROJECT(handle) + '/tags',
  PROJECT_TAGS_MANAGE: (handle: string) =>
    ROUTES.PROJECT_TAGS(handle) + '/manage',
  PROJECT_POST: (projectHandle: string, postHandle: string) =>
    ROUTES.PROJECT(projectHandle) + `/posts/${postHandle}`,
  PROJECT_POST_EDIT: (projectHandle: string, postHandle: string) =>
    ROUTES.PROJECT_POST(projectHandle, postHandle) + '/edit',
  NEW_PROJECT_POST: (handle: string) => ROUTES.PROJECT(handle) + '/posts/new',
  PROJECT_SETTINGS: (handle: string) => ROUTES.PROJECT(handle) + '/settings',
  PROJECT_SETTINGS_TEAMMATES: (handle: string) =>
    ROUTES.PROJECT(handle) + '/settings/teammates',
  PROJECT_SETTINGS_TEMPLATES: (handle: string) =>
    ROUTES.PROJECT(handle) + '/settings/templates',
  PROJECT_SETTINGS_TEMPLATES_NEW: (handle: string) =>
    ROUTES.PROJECT_SETTINGS_TEMPLATES(handle) + '/new',
  PROJECT_SETTINGS_TEMPLATE_EDIT: (handle: string, externalId: string) =>
    ROUTES.PROJECT_SETTINGS_TEMPLATES(handle) +
    `/${encodeURIComponent(externalId)}`,
  PROJECTS: () => '/projects',
  PROJECT_INVITATIONS: () => '/projects/invitations',
  NOTIFICATIONS: () => '/notifications',
  PROJECT_JOIN_REQUESTS: () => '/projects/join-requests',
  COLLECTION: (externalId: string) => `/collections/${externalId}`,
  COOKIES: () => '/cookies',
  PROFILE: (handle: string, params?: { tab?: string }) => {
    const base = `/@${handle}`;
    if (!params?.tab) return base;
    return `${base}?${new URLSearchParams({ tab: params.tab }).toString()}`;
  },
  PROFILE_FOLLOWERS: (handle: string) => ROUTES.PROFILE(handle) + '/followers',
  PROFILE_FOLLOWING: (handle: string) => ROUTES.PROFILE(handle) + '/following',
  PROFILE_DRAFTS: (handle: string) => ROUTES.PROFILE(handle, { tab: 'drafts' }),
  PROFILE_BOOKMARKS: (handle: string) =>
    ROUTES.PROFILE(handle, { tab: 'bookmarks' }),
  PROFILE_COLLECTIONS: (handle: string) =>
    ROUTES.PROFILE(handle, { tab: 'collections' }),
  SEARCH: (query?: string, projectHandle?: string) => {
    if (!query) return '/search';
    const params = new URLSearchParams({ q: query });
    if (projectHandle) params.set('projectHandle', projectHandle);
    return `/search?${params.toString()}`;
  },
  ADMIN: () => '/admin',
  ADMIN_POST_REPORTS: () => '/admin/post-reports',
  ADMIN_PROMOTIONS: () => '/admin/promotions',
  ADMIN_NEW_PROMOTION: () => '/admin/promotions/new',
  ADMIN_TAGS: () => '/admin/tags',
  ADMIN_USERS: () => '/admin/users',
  ADMIN_PROJECTS: () => '/admin/projects',
};

export default ROUTES;
