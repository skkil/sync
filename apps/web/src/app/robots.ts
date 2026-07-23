import type { MetadataRoute } from 'next';

import { getSiteUrl } from '@/lib/seo';

const NON_PUBLIC_PATHS = [
  '/admin',
  '/auth',
  '/onboarding',
  '/bookmarks',
  '/search',
  '/posts/drafts',
  '/posts/new',
  '/posts/*/edit',
  '/projects/new',
  '/projects/invitations',
  '/projects/*/settings',
  '/projects/*/posts/bookmarks',
  '/projects/*/posts/drafts',
  '/projects/*/posts/my',
  '/projects/*/posts/my-comments',
  '/projects/*/posts/new',
  '/projects/*/posts/*/edit',
];

export default function robots(): MetadataRoute.Robots {
  return {
    rules: {
      userAgent: '*',
      allow: '/',
      disallow: NON_PUBLIC_PATHS,
    },
    host: getSiteUrl().origin,
    sitemap: getSiteUrl('/sitemap.xml').toString(),
  };
}
