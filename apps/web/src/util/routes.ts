const ROUTES = {
  HOME: () => '/',
  ABOUT: () => '/about',
  TERMS: () => '/terms',
  PRIVACY: () => '/privacy',
  EXPLORE: () => '/explore',
  LOGIN: () => '/auth/login',
  REGISTER: () => '/auth/register',
  ONBOARDING: () => '/onboarding',
  POST: (slug: string) => `/posts/${slug}`,
  NEW_POST: () => `/posts/new`,
  PROJECT: (handle: string) => `/projects/${handle}`,
  NEW_PROJECT: () => '/projects/new',
  PROJECT_POSTS: (handle: string) => ROUTES.PROJECT(handle) + '/posts',
  PROJECT_POST: (projectHandle: string, postHandle: string) =>
    ROUTES.PROJECT(projectHandle) + `/posts/${postHandle}`,
  NEW_PROJECT_POST: (handle: string) => ROUTES.PROJECT(handle) + '/posts/new',
  PROJECT_SETTINGS: (handle: string) => ROUTES.PROJECT(handle) + '/settings',
  PROJECT_SETTINGS_TEAMMATES: (handle: string) =>
    ROUTES.PROJECT(handle) + '/settings/teammates',
  PROJECTS: () => '/projects',
  BOOKMARKS: () => '/bookmarks',
  COOKIES: () => '/cookies',
  PROFILE: (handle: string) => `/@${handle}`,
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
