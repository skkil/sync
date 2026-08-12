'use client';

import { useTranslations } from 'next-intl';
import Link from 'next/link';

import { useGetSeriesForPost } from '@/api/__generated__/post-series/post-series';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

interface PostSeriesCardProps {
  slug: string;
}

/**
 * 상세 페이지의 댓글 위에 놓이는 시리즈 카드. 시리즈 정보는 게시글 상세 응답이 아니라
 * `GET /posts/{slug}/series` 에서 직접 가져온다. 게시글이 시리즈에 속하지 않으면
 * (`series` 가 없으면) 아무것도 렌더링하지 않는다. 속해 있으면 시리즈 이름과
 * "N번째 / 전체 M편", 그리고 순서대로 나열된 편 목록을 보여준다. 각 편은 해당 게시글로
 * 이동하는 링크이며, 열람 권한이 없는 편(slug 없음)은 링크 없이 표시만 된다.
 */
export function PostSeriesCard({ slug }: PostSeriesCardProps) {
  const t = useTranslations('components.post.viewer.series');

  const { data: seriesData } = useGetSeriesForPost(slug);
  const series = seriesData?.data.series;
  const seriesId = series?.externalId;
  const currentSeriesPostId = seriesData?.data.currentSeriesPostId;

  if (!series || !seriesId) {
    return null;
  }

  const items = seriesData?.data.posts ?? [];
  // 순서는 목록에서 직접 찾는다. denormalize 된 postCount 대신 실제 항목 수를 쓴다.
  const position =
    items.find((item) => item.seriesPostId === currentSeriesPostId)?.position ??
    0;

  // 한 시리즈는 단일 프로젝트로 묶이므로 시리즈의 프로젝트 핸들로 모든 편의 경로를 만든다.
  const href = (postSlug: string) =>
    series.projectHandle
      ? ROUTES.PROJECT_POST(series.projectHandle, postSlug)
      : ROUTES.POST(postSlug);

  return (
    <Card className="shrink-0">
      <CardHeader>
        <CardDescription>{t('label')}</CardDescription>
        <CardTitle className="text-base">{series.name}</CardTitle>
        <CardDescription>
          {t('part', { position, total: items.length })}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <ol className="flex flex-col gap-0.5">
          {items.map((item) => {
            const current = item.seriesPostId === currentSeriesPostId;
            const itemClassName = cn(
              'flex items-center gap-2 rounded-md px-2 py-1.5 text-sm',
              current && 'bg-accent font-medium',
            );
            const content = (
              <>
                <span className="w-5 shrink-0 text-right tabular-nums text-muted-foreground">
                  {item.position}
                </span>
                <span
                  className={cn(
                    'min-w-0 flex-1 truncate',
                    !item.title && 'italic text-muted-foreground',
                  )}
                >
                  {item.title ?? t('hidden')}
                </span>
              </>
            );

            return (
              <li key={item.seriesPostId}>
                {item.slug ? (
                  <Link
                    href={href(item.slug)}
                    className={cn(itemClassName, !current && 'hover:bg-muted')}
                  >
                    {content}
                  </Link>
                ) : (
                  <span className={itemClassName}>{content}</span>
                )}
              </li>
            );
          })}
        </ol>
      </CardContent>
    </Card>
  );
}
