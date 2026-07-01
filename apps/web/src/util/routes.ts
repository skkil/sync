const ROUTES = {
  POST: (slug: string) => `/posts/${slug}`,
  PROJECT: (handle: string) => `/projects/${handle}`,
  PROJECT_POST: (projectHandle: string, postHandle: string) =>
    ROUTES.PROJECT(projectHandle) + `/posts/${postHandle}`,
  PROJECT_MEMBERS: (handle: string) => ROUTES.PROJECT(handle) + '/members',
  PROJECT_SETTINGS: (handle: string) => ROUTES.PROJECT(handle) + '/settings',
};

export default ROUTES;
