import { getTranslations } from 'next-intl/server';

import SolutionChoicesCard from './SolutionChoicesCard';
import { MonoLabel } from './primitives';

const BEFORE_KEYS = ['blog', 'community', 'messenger'] as const;

const DOT_GRID = {
  backgroundImage: 'radial-gradient(currentColor 1px, transparent 1px)',
  backgroundSize: '32px 32px',
};

/** 1. 문제와 해결 — 흩어진 세 곳에서 하나의 공간으로. */
export default async function ProblemSolutionSection() {
  const t = await getTranslations('pages.about.problemSolution');

  return (
    <section className="relative overflow-hidden border-y border-border">
      <div
        className="pointer-events-none absolute inset-0 -z-10 opacity-[0.04]"
        style={DOT_GRID}
      />

      <div className="mx-auto w-full max-w-6xl px-6 py-24">
        <div className="mx-auto max-w-2xl text-center">
          <h2 className="whitespace-pre-line text-4xl font-medium leading-[1.12] tracking-tight md:text-5xl lg:text-[3.4rem]">
            {t('title')}
          </h2>
          <p className="mx-auto mt-6 max-w-lg whitespace-pre-line text-base leading-relaxed text-muted-foreground">
            {t('description')}
          </p>
        </div>

        <div className="mt-16 grid items-start gap-8 lg:grid-cols-[1fr_auto_1fr] lg:gap-6">
          <div>
            <div className="space-y-3">
              {BEFORE_KEYS.map((key) => (
                <BeforeCard
                  key={key}
                  label={t(`before.cards.${key}.label`)}
                  title={t(`before.cards.${key}.title`)}
                  description={t(`before.cards.${key}.description`)}
                />
              ))}
            </div>
          </div>

          <TurnArrow />

          <div>
            <h3 className="text-2xl font-medium tracking-tight md:text-3xl">
              {t('after.title')}
            </h3>
            <p className="mt-3 whitespace-pre-line text-sm leading-relaxed text-muted-foreground">
              {t('after.description')}
            </p>
            <div className="mt-5">
              <SolutionChoicesCard />
            </div>
          </div>
        </div>

        <p className="mx-auto mt-16 max-w-xl border-t border-border pt-8 text-center text-lg font-medium leading-relaxed">
          {t('closing')}
        </p>
      </div>
    </section>
  );
}

function BeforeCard({
  label,
  title,
  description,
}: {
  label: string;
  title: string;
  description: string;
}) {
  return (
    <div className="rounded-xl border border-dashed border-border bg-card/50 p-5">
      <MonoLabel>{label}</MonoLabel>
      <h3 className="mt-3 text-[15px] font-medium leading-snug">{title}</h3>
      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
        {description}
      </p>
    </div>
  );
}

/** 흩어진 쪽에서 모이는 쪽으로 넘어가는 지점. 모바일에서는 아래를 가리킨다. */
function TurnArrow() {
  return (
    <div
      aria-hidden
      className="flex items-center justify-center gap-3 lg:flex-col lg:self-center"
    >
      <span className="h-px w-10 bg-border lg:h-10 lg:w-px" />
      <svg
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        className="size-5 shrink-0 rotate-90 text-muted-foreground lg:rotate-0"
      >
        <path d="M4 12h16M14 6l6 6-6 6" />
      </svg>
      <span className="h-px w-10 bg-border lg:h-10 lg:w-px" />
    </div>
  );
}
