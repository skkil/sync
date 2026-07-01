const ROUTES = {
  PROJECT: (handle: string) => `/projects/${handle}`,
  PROJECT_MEMBERS: (handle: string) => ROUTES.PROJECT(handle) + '/members',
  PROJECT_SETTINGS: (handle: string) => ROUTES.PROJECT(handle) + '/settings',
};

export default ROUTES;
