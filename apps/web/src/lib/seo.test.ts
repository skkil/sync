import { describe, expect, it } from 'vitest';

import {
  type PostSeoSource,
  createPostMetadata,
  getPostCanonicalUrl,
  isPostIndexable,
} from './seo';

const FALLBACK_DESCRIPTION = '기본 설명';

function createPost(overrides: Partial<PostSeoSource> = {}): PostSeoSource {
  return {
    slug: 'spring-security-guide',
    title: 'Spring Security 접근 제어',
    preview: '공개 게시물의 접근 제어 정책을 설명합니다.',
    status: 'PUBLISHED',
    scope: 'PUBLIC',
    author: {
      name: '김개발',
      handle: 'developer',
    },
    tags: [{ name: 'spring' }, { name: 'security' }],
    previewMedia: [{ url: 'https://cdn.example.com/cover.png' }],
    createdAt: '2026-07-24T01:00:00Z',
    updatedAt: '2026-07-24T02:00:00Z',
    ...overrides,
  };
}

describe('게시물 SEO 정책', () => {
  it('개인 공개 게시물을 색인하고 개인 게시물 URL을 사용한다', () => {
    const post = createPost();
    const metadata = createPostMetadata(post, FALLBACK_DESCRIPTION);

    expect(isPostIndexable(post)).toBe(true);
    expect(getPostCanonicalUrl(post).toString()).toBe(
      'https://sync.example.com/posts/spring-security-guide',
    );
    expect(metadata.robots).toMatchObject({
      index: true,
      follow: true,
    });
    expect(metadata.alternates?.canonical).toEqual(
      new URL('https://sync.example.com/posts/spring-security-guide'),
    );
  });

  it('공개 프로젝트 게시물을 색인하고 프로젝트 URL을 사용한다', () => {
    const post = createPost({
      scope: 'WORKSPACE',
      project: {
        handle: 'sync',
        name: 'SYNC',
        isPublic: true,
      },
    });

    expect(isPostIndexable(post)).toBe(true);
    expect(getPostCanonicalUrl(post).toString()).toBe(
      'https://sync.example.com/projects/sync/posts/spring-security-guide',
    );
  });

  it('비공개 프로젝트 게시물을 색인하지 않는다', () => {
    const post = createPost({
      scope: 'WORKSPACE',
      project: {
        handle: 'private-team',
        name: '비공개 팀',
        isPublic: false,
      },
    });
    const metadata = createPostMetadata(post, FALLBACK_DESCRIPTION);

    expect(isPostIndexable(post)).toBe(false);
    expect(metadata.robots).toMatchObject({
      index: false,
      follow: false,
      noarchive: true,
    });
  });

  it('초안은 소속 공간과 관계없이 색인하지 않는다', () => {
    const personalDraft = createPost({ status: 'DRAFT' });
    const projectDraft = createPost({
      status: 'DRAFT',
      scope: 'WORKSPACE',
      project: {
        handle: 'sync',
        name: 'SYNC',
        isPublic: true,
      },
    });

    expect(isPostIndexable(personalDraft)).toBe(false);
    expect(isPostIndexable(projectDraft)).toBe(false);
  });

  it('제목이 없으면 미리보기로 제목을 만들고 메타데이터를 완성한다', () => {
    const post = createPost({
      title: null,
      preview: '가'.repeat(50),
    });
    const metadata = createPostMetadata(post, FALLBACK_DESCRIPTION);

    expect(metadata.title).toBe(`${'가'.repeat(39)}…`);
    expect(metadata.description).toBe('가'.repeat(50));
    expect(metadata.openGraph).toMatchObject({
      type: 'article',
      locale: 'ko_KR',
      siteName: 'sync',
      publishedTime: post.createdAt,
      modifiedTime: post.updatedAt,
      tags: ['spring', 'security'],
    });
    expect(metadata.twitter).toMatchObject({
      card: 'summary_large_image',
      images: ['https://cdn.example.com/cover.png'],
    });
  });
});
