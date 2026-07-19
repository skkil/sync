import { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { LinkButton } from '@/components/ui/button';
import { Copyright } from '@/components/ui/copyright';
import { Logo } from '@/components/ui/logo';
import { cn } from '@/lib/utils';
import ROUTES from '@/util/routes';

const MONO = 'font-mono tracking-tight';
const AVATAR_COLORS = ['bg-primary', 'bg-foreground/70', 'bg-amber-500'];

function Eyebrow({ children }: { children: React.ReactNode }) {
  return (
    <span
      className={cn(
        MONO,
        'inline-flex items-center gap-2 text-xs uppercase text-muted-foreground',
      )}
    >
      <span className="size-1.5 rounded-full bg-primary" />
      {children}
    </span>
  );
}

function TypeTag({ label }: { label: string }) {
  return (
    <span
      className={cn(
        MONO,
        'rounded-sm border border-border px-1.5 py-0.5 text-[10px] uppercase text-muted-foreground',
      )}
    >
      {label}
    </span>
  );
}

/* 시그니처 요소: "한 번 답하면 영원히 신뢰할 수 있는" 정본 답변 카드 */
function CanonicalCard({
  tag,
  badge,
  question,
  answerCode,
  answerText,
  verifiedAt,
  freshBadge,
}: {
  tag: string;
  badge: string;
  question: string;
  answerCode: string;
  answerText: string;
  verifiedAt: string;
  freshBadge: string;
}) {
  return (
    <div className="rounded-xl border border-border bg-card p-5 shadow-sm">
      <div className="flex items-center justify-between">
        <span
          className={cn(MONO, 'text-[11px] uppercase text-muted-foreground')}
        >
          {tag}
        </span>
        <span className="flex items-center gap-1.5 rounded-full bg-primary/10 px-2 py-0.5 text-[11px] font-medium text-primary">
          <svg viewBox="0 0 24 24" className="size-3" fill="none">
            <path
              d="M20 6 9 17l-5-5"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
          {badge}
        </span>
      </div>

      <p className="mt-3 text-[15px] font-medium leading-snug">{question}</p>
      <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
        <code className="rounded bg-muted px-1 py-0.5 text-[13px]">
          {answerCode}
        </code>{' '}
        {answerText}
      </p>

      <div className="mt-4 flex items-center justify-between border-t border-border pt-3">
        <span className={cn(MONO, 'text-[11px] text-muted-foreground')}>
          {verifiedAt}
        </span>
        <span className="flex items-center gap-1.5 text-[11px] font-medium text-primary">
          <span className="size-1.5 animate-pulse rounded-full bg-primary" />
          {freshBadge}
        </span>
      </div>
    </div>
  );
}

function BentoTile({
  className,
  eyebrow,
  title,
  children,
}: {
  className?: string;
  eyebrow: string;
  title: string;
  children?: React.ReactNode;
}) {
  return (
    <div
      className={cn(
        'flex flex-col rounded-xl border border-border bg-card p-6',
        className,
      )}
    >
      <span className={cn(MONO, 'text-[11px] uppercase text-muted-foreground')}>
        {eyebrow}
      </span>
      <h3 className="mt-2 text-lg font-medium leading-snug">{title}</h3>
      {children}
    </div>
  );
}

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.about');

  return { title: t('metaTitle') };
}

export default async function About() {
  const t = await getTranslations('pages.about');
  const tPostType = await getTranslations('components.post.type');

  const freshnessRows = [
    {
      label: t('bento.items.freshness.rows.apiAuth.label'),
      state: t('bento.items.freshness.rows.apiAuth.state'),
      fresh: true,
    },
    {
      label: t('bento.items.freshness.rows.onboarding.label'),
      state: t('bento.items.freshness.rows.onboarding.state'),
      fresh: true,
    },
    {
      label: t('bento.items.freshness.rows.legacyDeploy.label'),
      state: t('bento.items.freshness.rows.legacyDeploy.state'),
      fresh: false,
    },
  ];

  const postTypeCards = [
    {
      tag: 'SHORT',
      title: tPostType('SHORT'),
      desc: t('postTypes.items.short.description'),
    },
    {
      tag: 'QUESTION',
      title: tPostType('QUESTION'),
      desc: t('postTypes.items.question.description'),
    },
    {
      tag: 'LONG',
      title: tPostType('LONG'),
      desc: t('postTypes.items.long.description'),
    },
  ];

  return (
    <div className="flex min-h-screen w-full flex-col bg-background text-foreground">
      {/* Nav */}
      <header className="sticky top-0 z-20 border-b border-border/70 bg-background/80 backdrop-blur">
        <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-4">
          <Logo />
          <div className="flex items-center gap-2">
            <LinkButton variant="ghost" href={ROUTES.LOGIN()}>
              {t('nav.login')}
            </LinkButton>
            <LinkButton href={ROUTES.REGISTER()}>
              {t('nav.register')}
            </LinkButton>
          </div>
        </div>
      </header>

      <main className="flex-1">
        {/* Hero */}
        <section className="relative overflow-hidden">
          <div
            className="pointer-events-none absolute inset-0 -z-10 opacity-[0.04]"
            style={{
              backgroundImage:
                'radial-gradient(currentColor 1px, transparent 1px)',
              backgroundSize: '32px 32px',
            }}
          />
          <div className="mx-auto grid w-full max-w-6xl items-center gap-12 px-6 pb-20 pt-20 lg:grid-cols-[1.1fr_0.9fr] lg:pt-28">
            <div>
              <Eyebrow>{t('hero.badge')}</Eyebrow>
              <h1 className="mt-5 text-4xl font-medium leading-[1.08] tracking-tight md:text-5xl lg:text-[3.4rem]">
                {t('hero.title')}
              </h1>
              <p className="mt-6 max-w-md text-base leading-relaxed text-muted-foreground">
                {t('hero.description')}
              </p>
              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                <LinkButton size="lg" href={ROUTES.REGISTER()}>
                  {t('hero.actions.register')}
                </LinkButton>
              </div>
              <p className={cn(MONO, 'mt-6 text-[11px] text-muted-foreground')}>
                {t('hero.audience')}
              </p>
            </div>

            <CanonicalCard
              tag={t('hero.demo.tag')}
              badge={t('hero.demo.badge')}
              question={t('hero.demo.question')}
              answerCode="make deploy staging"
              answerText={t('hero.demo.answer')}
              verifiedAt={t('hero.demo.verifiedAt')}
              freshBadge={t('hero.demo.freshBadge')}
            />
          </div>
        </section>

        {/* Bento */}
        <section className="mx-auto w-full max-w-6xl px-6 pb-24">
          <div className="mb-10 flex flex-col gap-3">
            <Eyebrow>{t('bento.eyebrow')}</Eyebrow>
            <h2 className="max-w-2xl text-2xl font-medium tracking-tight md:text-3xl">
              {t('bento.title')}
              <br className="hidden md:block" /> {t('bento.titleTail')}
            </h2>
          </div>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-6">
            {/* 시그니처: 신선도/신뢰 시스템 */}
            <BentoTile
              className="md:col-span-3 md:row-span-2"
              eyebrow={t('bento.items.freshness.eyebrow')}
              title={t('bento.items.freshness.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.freshness.description')}
              </p>

              <div className="mt-auto space-y-2 pt-6">
                {freshnessRows.map((row) => (
                  <div
                    key={row.label}
                    className="flex items-center justify-between rounded-lg border border-border bg-background px-3 py-2.5"
                  >
                    <span className="text-sm">{row.label}</span>
                    <span
                      className={cn(
                        MONO,
                        'flex items-center gap-1.5 text-[11px]',
                        row.fresh ? 'text-primary' : 'text-amber-600',
                      )}
                    >
                      <span
                        className={cn(
                          'size-1.5 rounded-full',
                          row.fresh ? 'bg-primary' : 'bg-amber-500',
                        )}
                      />
                      {row.state}
                    </span>
                  </div>
                ))}
              </div>
            </BentoTile>

            {/* 검색 & 관련도 */}
            <BentoTile
              className="md:col-span-3"
              eyebrow={t('bento.items.search.eyebrow')}
              title={t('bento.items.search.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.search.description')}
              </p>
              <div className="mt-4 flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2">
                <svg
                  viewBox="0 0 24 24"
                  className="size-4 text-muted-foreground"
                  fill="none"
                >
                  <circle
                    cx="11"
                    cy="11"
                    r="7"
                    stroke="currentColor"
                    strokeWidth="2"
                  />
                  <path
                    d="m20 20-3-3"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                  />
                </svg>
                <span className="text-sm text-muted-foreground">
                  {t('bento.items.search.example')}
                </span>
                <span className={cn(MONO, 'ml-auto text-[10px] text-primary')}>
                  {t('bento.items.search.latency')}
                </span>
              </div>
            </BentoTile>

            {/* 실시간 */}
            <BentoTile
              className="md:col-span-3"
              eyebrow={t('bento.items.realtime.eyebrow')}
              title={t('bento.items.realtime.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.realtime.description')}
              </p>
              <div className="mt-4 flex items-center gap-2">
                <div className="flex -space-x-2">
                  {AVATAR_COLORS.map((c, i) => (
                    <span
                      key={i}
                      className={cn(
                        'size-6 rounded-full border-2 border-card',
                        c,
                      )}
                    />
                  ))}
                </div>
                <span className={cn(MONO, 'text-[11px] text-muted-foreground')}>
                  {t('bento.items.realtime.activeUsers')}
                </span>
              </div>
            </BentoTile>

            {/* 백링크 */}
            <BentoTile
              className="md:col-span-2"
              eyebrow={t('bento.items.backlinks.eyebrow')}
              title={t('bento.items.backlinks.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.backlinks.description')}
              </p>
            </BentoTile>

            {/* 정본 답변 */}
            <BentoTile
              className="md:col-span-2"
              eyebrow={t('bento.items.canonical.eyebrow')}
              title={t('bento.items.canonical.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.canonical.description')}
              </p>
            </BentoTile>

            {/* 채팅 인제스천 */}
            <BentoTile
              className="md:col-span-2"
              eyebrow={t('bento.items.chatIngestion.eyebrow')}
              title={t('bento.items.chatIngestion.title')}
            >
              <p className="mt-3 text-sm leading-relaxed text-muted-foreground">
                {t('bento.items.chatIngestion.description')}
              </p>
            </BentoTile>
          </div>
        </section>

        {/* 세 가지 글 유형 */}
        <section className="border-y border-border bg-muted/30">
          <div className="mx-auto w-full max-w-6xl px-6 py-20">
            <Eyebrow>{t('postTypes.eyebrow')}</Eyebrow>
            <h2 className="mt-4 max-w-2xl text-2xl font-medium tracking-tight md:text-3xl">
              {t('postTypes.title')}
            </h2>
            <div className="mt-10 grid grid-cols-1 gap-4 md:grid-cols-3">
              {postTypeCards.map((c) => (
                <div
                  key={c.tag}
                  className="rounded-xl border border-border bg-card p-6"
                >
                  <TypeTag label={c.tag} />
                  <h3 className="mt-4 text-lg font-medium">{c.title}</h3>
                  <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
                    {c.desc}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* 최종 CTA */}
        <section className="mx-auto w-full max-w-6xl px-6 py-24">
          <div className="relative overflow-hidden rounded-2xl border border-border bg-card px-8 py-16 text-center">
            <div
              className="pointer-events-none absolute inset-0 -z-10 opacity-[0.05]"
              style={{
                backgroundImage:
                  'radial-gradient(circle at 50% 0%, var(--color-brand), transparent 60%)',
              }}
            />
            <Eyebrow>{t('finalCta.eyebrow')}</Eyebrow>
            <h2 className="mx-auto mt-5 max-w-xl text-3xl font-medium tracking-tight md:text-4xl">
              {t('finalCta.title')}
            </h2>
            <p className="mx-auto mt-4 max-w-md text-muted-foreground">
              {t('finalCta.description')}
            </p>
            <div className="mt-8 flex flex-col justify-center gap-3 sm:flex-row">
              <LinkButton size="lg" href={ROUTES.REGISTER()}>
                {t('finalCta.action')}
              </LinkButton>
            </div>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-border">
        <div className="mx-auto grid w-full max-w-6xl grid-cols-2 gap-8 px-6 py-12 md:grid-cols-4">
          <div className="col-span-2 md:col-span-2">
            <Logo />
            <p className="mt-3 max-w-[260px] text-sm text-muted-foreground">
              {t('footer.description')}
            </p>
          </div>
          <div>
            <h4
              className={cn(
                MONO,
                'mb-3 text-[11px] uppercase text-muted-foreground',
              )}
            >
              {t('footer.legal.title')}
            </h4>
            <ul className="flex flex-col gap-2 text-sm text-muted-foreground">
              <li>
                <Link href={ROUTES.TERMS()} className="hover:text-foreground">
                  {t('footer.legal.terms')}
                </Link>
              </li>
              <li>
                <Link href={ROUTES.PRIVACY()} className="hover:text-foreground">
                  {t('footer.legal.privacy')}
                </Link>
              </li>
            </ul>
          </div>
          <div>
            <h4
              className={cn(
                MONO,
                'mb-3 text-[11px] uppercase text-muted-foreground',
              )}
            >
              {t('footer.account.title')}
            </h4>
            <ul className="flex flex-col gap-2 text-sm text-muted-foreground">
              <li>
                <Link
                  href={ROUTES.REGISTER()}
                  className="hover:text-foreground"
                >
                  {t('footer.account.register')}
                </Link>
              </li>
              <li>
                <Link href={ROUTES.LOGIN()} className="hover:text-foreground">
                  {t('footer.account.login')}
                </Link>
              </li>
            </ul>
          </div>
        </div>
        <div className="mx-auto w-full max-w-6xl border-t border-border px-6 py-6">
          <Copyright />
        </div>
      </footer>
    </div>
  );
}
