import type { Metadata } from 'next';

import { env } from '@/lib/env';
import ROUTES from '@/util/routes';

const SITE_NAME = 'sync';
const POST_TITLE_MAX_LENGTH = 60;
const POST_PREVIEW_TITLE_MAX_LENGTH = 40;
const POST_DESCRIPTION_MAX_LENGTH = 160;

export interface PostSeoSource {
  slug: string;
  title?: string | null;
  preview: string;
  status: string;
  scope: string;
  author: {
    name: string;
    handle: string;
  };
  project?: {
    handle?: string | null;
    name?: string | null;
    isPublic?: boolean | null;
  };
  tags: Array<{
    name: string;
  }>;
  previewMedia: Array<{
    url: string;
  }>;
  createdAt: string;
  updatedAt: string;
}

export const INDEXABLE_ROBOTS = {
  index: true,
  follow: true,
  googleBot: {
    index: true,
    follow: true,
    'max-image-preview': 'large',
    'max-snippet': -1,
    'max-video-preview': -1,
  },
} satisfies NonNullable<Metadata['robots']>;

export const NON_INDEXABLE_ROBOTS = {
  index: false,
  follow: false,
  noarchive: true,
  nosnippet: true,
  noimageindex: true,
  googleBot: {
    index: false,
    follow: false,
    noarchive: true,
    nosnippet: true,
    noimageindex: true,
  },
} satisfies NonNullable<Metadata['robots']>;

export const NON_INDEXABLE_METADATA = {
  robots: NON_INDEXABLE_ROBOTS,
} satisfies Metadata;

export function getSiteUrl(path = '/'): URL {
  return new URL(path, new URL('/', env.BETTER_AUTH_URL));
}

export function getPostCanonicalPath(post: PostSeoSource): string {
  const projectHandle = post.project?.handle;

  return projectHandle
    ? ROUTES.PROJECT_POST(projectHandle, post.slug)
    : ROUTES.POST(post.slug);
}

export function getPostCanonicalUrl(post: PostSeoSource): URL {
  return getSiteUrl(getPostCanonicalPath(post));
}

export function isPostIndexable(post: PostSeoSource): boolean {
  if (post.status !== 'PUBLISHED') {
    return false;
  }

  if (post.scope === 'PUBLIC') {
    return true;
  }

  return (
    post.scope === 'WORKSPACE' &&
    post.project?.isPublic === true &&
    Boolean(post.project.handle)
  );
}

export function createPostMetadata(
  post: PostSeoSource,
  fallbackDescription: string,
): Metadata {
  const title =
    truncate(post.title, POST_TITLE_MAX_LENGTH) ||
    truncate(post.preview, POST_PREVIEW_TITLE_MAX_LENGTH) ||
    SITE_NAME;
  const description =
    truncate(post.preview, POST_DESCRIPTION_MAX_LENGTH) || fallbackDescription;
  const canonicalUrl = getPostCanonicalUrl(post);
  const authorUrl = getSiteUrl(ROUTES.PROFILE(post.author.handle));
  const tags = post.tags.map((tag) => tag.name);
  const images = post.previewMedia.slice(0, 1).map((media) => ({
    url: media.url,
    alt: title,
  }));

  return {
    title,
    description,
    authors: [{ name: post.author.name, url: authorUrl }],
    creator: post.author.name,
    publisher: SITE_NAME,
    keywords: tags,
    alternates: {
      canonical: canonicalUrl,
    },
    robots: isPostIndexable(post) ? INDEXABLE_ROBOTS : NON_INDEXABLE_ROBOTS,
    openGraph: {
      type: 'article',
      locale: 'ko_KR',
      siteName: SITE_NAME,
      title,
      description,
      url: canonicalUrl,
      publishedTime: post.createdAt,
      modifiedTime: post.updatedAt,
      authors: [authorUrl],
      section: post.project?.name ?? undefined,
      tags,
      images,
    },
    twitter: {
      card: images.length > 0 ? 'summary_large_image' : 'summary',
      title,
      description,
      images: images.map((image) => image.url),
    },
  };
}

function truncate(value: string | null | undefined, maxLength: number): string {
  const normalized = value?.replace(/\s+/g, ' ').trim() ?? '';

  if (normalized.length <= maxLength) {
    return normalized;
  }

  return `${normalized.slice(0, maxLength - 1).trimEnd()}…`;
}
