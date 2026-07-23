import type { MetadataRoute } from 'next';

import type {
  GetPostsResponse,
  GetPostsResponsePostsNodesItemContent,
} from '@/api/__generated__/types';
import { env } from '@/lib/env';
import { getPostCanonicalUrl, getSiteUrl, isPostIndexable } from '@/lib/seo';
import ROUTES from '@/util/routes';

const PAGE_SIZE = '100';
const MAXIMUM_POST_COUNT = 49_997;
const SITEMAP_REVALIDATE_SECONDS = 60 * 60;

export const dynamic = 'force-dynamic';
export const revalidate = 3600;

const STATIC_ENTRIES: MetadataRoute.Sitemap = [
  {
    url: getSiteUrl(ROUTES.ABOUT()).toString(),
    changeFrequency: 'monthly',
    priority: 0.8,
  },
  {
    url: getSiteUrl(ROUTES.TERMS()).toString(),
    changeFrequency: 'yearly',
    priority: 0.2,
  },
  {
    url: getSiteUrl(ROUTES.PRIVACY()).toString(),
    changeFrequency: 'yearly',
    priority: 0.2,
  },
];

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  try {
    const posts = await getPublicPosts();

    return [
      ...STATIC_ENTRIES,
      ...posts.map((post) => ({
        url: getPostCanonicalUrl(post).toString(),
        lastModified: new Date(post.updatedAt),
        changeFrequency: 'weekly' as const,
        priority: 0.7,
      })),
    ];
  } catch (error) {
    console.error('공개 게시물 사이트맵 생성에 실패했습니다.', error);
    return STATIC_ENTRIES;
  }
}

async function getPublicPosts() {
  const posts: GetPostsResponsePostsNodesItemContent[] = [];
  const visitedCursors = new Set<string>();
  let after: string | undefined;

  while (posts.length < MAXIMUM_POST_COUNT) {
    const page = (await getPostPage(after)).posts;

    if (!page) {
      break;
    }

    for (const node of page.nodes) {
      if (isPostIndexable(node.content)) {
        posts.push(node.content);
      }

      if (posts.length >= MAXIMUM_POST_COUNT) {
        break;
      }
    }

    const nextCursor = page.pageInfo.endCursor ?? undefined;
    if (
      !page.pageInfo.hasNextPage ||
      !nextCursor ||
      visitedCursors.has(nextCursor)
    ) {
      break;
    }

    visitedCursors.add(nextCursor);
    after = nextCursor;
  }

  return posts;
}

async function getPostPage(after?: string): Promise<GetPostsResponse> {
  const baseUrl = env.NEXT_PUBLIC_BACKEND_URL.endsWith('/')
    ? env.NEXT_PUBLIC_BACKEND_URL
    : `${env.NEXT_PUBLIC_BACKEND_URL}/`;
  const url = new URL('posts', baseUrl);
  url.searchParams.set('first', PAGE_SIZE);

  if (after) {
    url.searchParams.set('after', after);
  }

  const response = await fetch(url, {
    next: {
      revalidate: SITEMAP_REVALIDATE_SECONDS,
    },
  });

  if (!response.ok) {
    throw new Error(
      `공개 게시물 조회에 실패했습니다. 상태 코드: ${response.status}`,
    );
  }

  return response.json() as Promise<GetPostsResponse>;
}
