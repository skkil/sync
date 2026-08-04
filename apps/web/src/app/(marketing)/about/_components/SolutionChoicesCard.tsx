import { getTranslations } from 'next-intl/server';

import { cn } from '@/lib/utils';

import { MONO, MonoLabel, ResolvedPill, TypeTag } from './primitives';

/** 글 유형과 게시 위치, 두 번의 선택과 그 결과 만들어지는 글. */
export default async function SolutionChoicesCard() {
  const t = await getTranslations('pages.about.problemSolution.choices');

  return (
    <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
      <div>
        <div className="flex flex-wrap gap-1.5">
          <TypeTag label={t('write.short')} />
          <TypeTag active label={t('write.question')} />
          <TypeTag label={t('write.long')} />
        </div>
        <p className="mt-2 text-xs text-muted-foreground">{t('write.hint')}</p>
      </div>

      <div className="mt-5">
        <div className="flex flex-wrap gap-1.5">
          <TypeTag label={t('place.personal')} />
          <TypeTag active label={t('place.project')} />
        </div>
        <p className="mt-2 text-xs text-muted-foreground">{t('place.hint')}</p>
      </div>

      <SolutionDemoPost />
    </div>
  );
}

/** 위 두 선택의 결과로 만들어지는, 해결된 질문 글. */
async function SolutionDemoPost() {
  const t = await getTranslations('pages.about.problemSolution.demo');

  return (
    <div className="mt-6 rounded-lg border border-border bg-background p-4">
      <div className="flex items-center justify-between gap-2">
        <MonoLabel>{t('tag')}</MonoLabel>
        <ResolvedPill className="gap-1.5 text-[11px]">
          {t('resolvedBadge')}
        </ResolvedPill>
      </div>

      <p className="mt-3 text-[15px] font-medium leading-snug">
        {t('question')}
      </p>

      <div className="mt-3 border-l-2 border-primary/40 pl-3">
        <span className={cn(MONO, 'text-[10px] uppercase text-primary')}>
          {t('acceptedBadge')}
        </span>
        <p className="mt-1 text-sm leading-relaxed text-muted-foreground">
          <code className="rounded bg-muted px-1 py-0.5 text-[13px]">
            {t('answerCode')}
          </code>{' '}
          {t('answer')}
        </p>
      </div>
    </div>
  );
}
