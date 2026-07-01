import {
  ArrowRightIcon,
  BookmarkSimpleIcon,
  ChatCircleIcon,
  GlobeHemisphereEastIcon,
  HashIcon,
  LockKeyIcon,
  MagnifyingGlassIcon,
  TagIcon,
} from '@phosphor-icons/react/dist/ssr';
import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/ui/logo';
import { cn } from '@/lib/utils';

type PreviewPost = {
  type: string;
  author: string;
  time: string;
  title: string;
  tag: string;
};

type TrendingPost = {
  type: string;
  author: string;
  time: string;
  title: string;
  description: string;
  tags: string[];
  score: string;
  comments: string;
};

type TagItem = {
  name: string;
  count: string;
};

type ModeItem = {
  title: string;
  description: string;
};

type FooterColumn = {
  title: string;
  links: string[];
};

export default async function LandingPage() {
  const t = await getTranslations('pages.landing');
  const previewPosts = t.raw('preview.posts') as PreviewPost[];
  const trendingPosts = t.raw('trending.posts') as TrendingPost[];
  const tags = t.raw('tags.items') as TagItem[];
  const modes = t.raw('modes.items') as ModeItem[];
  const footerColumns = t.raw('footer.columns') as FooterColumn[];

  return (
    <main
      id="top"
      className="min-h-screen bg-background font-sans text-foreground"
    >
      <AnnouncementBar
        text={t('announcement.text')}
        linkLabel={t('announcement.link')}
      />
      <MarketingHeader t={t} />

      <section className="border-b bg-muted/25">
        <div className="mx-auto flex max-w-7xl flex-col items-center gap-9 px-4 py-11 text-center sm:px-6 sm:py-15 lg:px-8 lg:py-18">
          <div className="flex max-w-4xl flex-col items-center gap-5">
            <Badge
              variant="outline"
              className="h-7 max-w-full bg-background px-3 font-medium"
            >
              <GlobeHemisphereEastIcon />
              {t('hero.eyebrow')}
            </Badge>
            <div className="space-y-4">
              <h1 className="mx-auto max-w-4xl text-4xl leading-[1.12] font-semibold [text-wrap:balance] [word-break:keep-all] sm:text-5xl lg:text-[4rem]">
                {t('hero.title')}
              </h1>
              <p className="mx-auto max-w-2xl text-base leading-7 text-muted-foreground [text-wrap:pretty] [word-break:keep-all]">
                {t('hero.description')}
              </p>
            </div>
            <div className="flex flex-col items-center gap-3 sm:flex-row">
              <PrimaryCtaLink href="/auth/register">
                {t('hero.primary')}
              </PrimaryCtaLink>
              <Button asChild variant="outline" size="lg">
                <Link href="#trending">{t('hero.secondary')}</Link>
              </Button>
            </div>
          </div>

          <ProductPreview
            t={t}
            previewPosts={previewPosts}
            className="w-full max-w-5xl"
          />
        </div>
      </section>

      <section id="trending" className="border-b py-16 sm:py-20">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <SectionHeader
            title={t('trending.title')}
            description={t('trending.description')}
            action={t('trending.action')}
            href="#tags"
          />
          <div className="mt-9 grid gap-4 md:grid-cols-3">
            {trendingPosts.map((post, index) => (
              <TrendingPostCard key={post.title} post={post} index={index} />
            ))}
          </div>
        </div>
      </section>

      <section id="tags" className="border-b bg-muted/25 py-16 sm:py-20">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <SectionHeader
            title={t('tags.title')}
            description={t('tags.description')}
            action={t('tags.action')}
            href="#modes"
          />
          <div className="mt-8 flex flex-wrap gap-2.5">
            {tags.map((tag) => (
              <Link
                key={tag.name}
                href="#trending"
                className="inline-flex h-10 items-center gap-2 rounded-[6px] border bg-background px-3.5 text-sm font-medium shadow-sm transition-colors hover:border-primary/40 hover:bg-primary/5"
              >
                <HashIcon className="size-4 text-primary" />
                <span>{tag.name.replace('#', '')}</span>
                <span className="font-mono text-xs text-muted-foreground">
                  {tag.count}
                </span>
              </Link>
            ))}
          </div>
        </div>
      </section>

      <section id="modes" className="border-b py-16 sm:py-20">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h2 className="text-center text-3xl leading-tight font-semibold [text-wrap:balance] [word-break:keep-all] sm:text-4xl">
            {t('modes.title')}
          </h2>
          <div className="mt-9 grid gap-4 md:grid-cols-3">
            {modes.map((mode, index) => (
              <ModeCard key={mode.title} mode={mode} index={index} />
            ))}
          </div>
        </div>
      </section>

      <section className="bg-foreground py-16 text-background sm:py-20">
        <div className="mx-auto flex max-w-4xl flex-col items-center gap-6 px-4 text-center sm:px-6 lg:px-8">
          <h2 className="text-3xl leading-tight font-semibold [text-wrap:balance] [word-break:keep-all] sm:text-4xl">
            {t('cta.title')}
          </h2>
          <p className="max-w-2xl text-sm leading-6 text-background/70 sm:text-base">
            {t('cta.description')}
          </p>
          <div className="flex flex-col gap-3 sm:flex-row">
            <Button asChild size="lg">
              <Link href="/auth/register">{t('cta.primary')}</Link>
            </Button>
            <Button
              asChild
              size="lg"
              variant="outline"
              className="border-background/25 bg-transparent text-background hover:bg-background/10 hover:text-background"
            >
              <Link href="#footer">{t('cta.secondary')}</Link>
            </Button>
          </div>
        </div>
      </section>

      <MarketingFooter
        description={t('footer.description')}
        columns={footerColumns}
      />
    </main>
  );
}

function AnnouncementBar({
  text,
  linkLabel,
}: {
  text: string;
  linkLabel: string;
}) {
  return (
    <div className="border-b bg-foreground text-background">
      <Link
        href="#modes"
        className="mx-auto flex min-h-9 max-w-7xl items-center justify-center gap-2 px-4 py-1.5 text-center text-[13px] leading-5 font-medium sm:px-6"
      >
        <span>{text}</span>
        <span className="inline-flex items-center gap-1 text-primary">
          {linkLabel}
          <ArrowRightIcon className="size-3" />
        </span>
      </Link>
    </div>
  );
}

function MarketingHeader({
  t,
}: {
  t: Awaited<ReturnType<typeof getTranslations>>;
}) {
  const navItems = [
    { label: t('nav.explore'), href: '#trending' },
    { label: t('nav.tags'), href: '#tags' },
    { label: t('nav.pricing'), href: '#modes' },
    { label: t('nav.help'), href: '#footer' },
  ];

  return (
    <header className="sticky top-0 z-20 border-b bg-background/95 backdrop-blur">
      <div className="mx-auto flex h-14 max-w-7xl items-center gap-3 px-4 sm:px-6 lg:px-8">
        <Link href="/" aria-label="sync">
          <Logo />
        </Link>

        <nav className="hidden items-center gap-1 md:ml-4 md:flex">
          {navItems.map((item) => (
            <Button key={item.href} asChild variant="ghost" size="sm">
              <Link href={item.href}>{item.label}</Link>
            </Button>
          ))}
        </nav>

        <Link
          href="#trending"
          className="ml-auto hidden h-9 w-full max-w-sm items-center gap-2 rounded-md border bg-muted/35 px-3 text-sm text-muted-foreground transition-colors hover:bg-muted lg:flex"
        >
          <MagnifyingGlassIcon className="size-4" />
          <span className="truncate">{t('nav.search')}</span>
        </Link>

        <div className="ml-auto flex items-center gap-1.5 lg:ml-0">
          <Button asChild variant="ghost" size="sm">
            <Link href="/auth/login">{t('nav.login')}</Link>
          </Button>
          <Button asChild size="sm">
            <Link href="/auth/register">{t('nav.signup')}</Link>
          </Button>
        </div>
      </div>
    </header>
  );
}

function ProductPreview({
  t,
  previewPosts,
  className,
}: {
  t: Awaited<ReturnType<typeof getTranslations>>;
  previewPosts: PreviewPost[];
  className?: string;
}) {
  return (
    <div
      className={cn(
        'overflow-hidden rounded-[8px] border bg-background text-left shadow-[0_20px_60px_-34px_rgb(0_0_0/0.45)]',
        className,
      )}
    >
      <div className="flex h-10 items-center gap-2 border-b bg-muted/25 px-3">
        <div className="flex gap-1.5">
          <span className="size-2 rounded-full bg-destructive/60" />
          <span className="size-2 rounded-full bg-amber-400" />
          <span className="size-2 rounded-full bg-primary" />
        </div>
        <div className="ml-2 flex-1 rounded-md bg-muted px-3 py-1 font-mono text-xs text-muted-foreground">
          {t('hero.previewUrl')}
        </div>
      </div>

      <div className="grid min-h-64 grid-cols-1 sm:grid-cols-[9rem_1fr] lg:grid-cols-[10rem_1fr_13rem]">
        <aside className="hidden border-r bg-muted/25 p-4 sm:block">
          <div className="mb-4 text-xs font-semibold text-muted-foreground">
            {t('preview.browseLabel')}
          </div>
          <div className="space-y-1 text-sm">
            <div className="rounded-md bg-background px-2 py-1 font-medium">
              {t('preview.sidebar.home')}
            </div>
            <div className="px-2 py-1 text-muted-foreground">
              {t('preview.sidebar.explore')}
            </div>
            <div className="px-2 py-1 text-muted-foreground">
              {t('preview.sidebar.tags')}
            </div>
          </div>
        </aside>

        <div className="p-4 sm:p-5">
          <div className="mb-4 flex items-center justify-between gap-3">
            <div className="flex gap-2 text-sm font-medium">
              <span>{t('preview.tabs.latest')}</span>
              <span className="text-muted-foreground">
                {t('preview.tabs.top')}
              </span>
            </div>
            <Badge variant="outline" className="hidden sm:inline-flex">
              <TagIcon />
              {previewPosts[0]?.tag}
            </Badge>
          </div>

          <div className="space-y-3">
            {previewPosts.map((post, index) => (
              <div
                key={post.title}
                className={cn(
                  'rounded-r-[6px] border-l-2 bg-muted/20 py-3 pr-3 pl-4',
                  index === 0 ? 'border-primary' : 'border-blue-500',
                )}
              >
                <div className="mb-1 flex flex-wrap items-center gap-2 font-mono text-xs text-muted-foreground">
                  <span className="rounded bg-primary/10 px-1.5 py-0.5 text-primary">
                    {post.type}
                  </span>
                  <span>{post.author}</span>
                  <span>{post.time}</span>
                  <span className="text-primary">{post.tag}</span>
                </div>
                <p className="text-sm font-semibold">{post.title}</p>
              </div>
            ))}
          </div>
        </div>

        <aside className="hidden border-l bg-muted/20 p-4 lg:block">
          <div className="rounded-[6px] border bg-background p-3 shadow-sm">
            <div className="mb-2 text-xs font-semibold text-primary">
              {t('preview.workspace.title')}
            </div>
            <p className="text-xs leading-5 text-muted-foreground">
              {t('preview.workspace.description')}
            </p>
          </div>
          <div className="mt-3 rounded-[6px] border bg-background p-3 shadow-sm">
            <div className="mb-2 text-xs font-semibold">
              {t('preview.tagPanel.title')}
            </div>
            <div className="flex flex-wrap gap-1.5">
              {(t.raw('preview.tagPanel.items') as string[]).map((tag) => (
                <Badge key={tag} variant="secondary" className="font-mono">
                  {tag}
                </Badge>
              ))}
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
}

function SectionHeader({
  title,
  description,
  action,
  href,
}: {
  title: string;
  description: string;
  action: string;
  href: string;
}) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div className="max-w-2xl">
        <h2 className="text-3xl leading-tight font-semibold [text-wrap:balance] [word-break:keep-all] sm:text-4xl">
          {title}
        </h2>
        <p className="mt-3 text-sm leading-6 text-muted-foreground sm:text-base">
          {description}
        </p>
      </div>
      <Link
        href={href}
        className="inline-flex w-fit items-center gap-1 text-sm font-medium transition-colors hover:text-primary"
      >
        <span>{action}</span>
        <ArrowRightIcon className="size-4 shrink-0" />
      </Link>
    </div>
  );
}

function PrimaryCtaLink({
  href,
  children,
}: {
  href: string;
  children: string;
}) {
  return (
    <Link
      href={href}
      className="inline-flex h-10 items-center justify-center gap-2 rounded-4xl border border-transparent bg-primary px-4 text-sm font-medium whitespace-nowrap text-primary-foreground transition-colors hover:bg-primary/80"
    >
      <span>{children}</span>
      <ArrowRightIcon className="size-4 shrink-0" />
    </Link>
  );
}

function TrendingPostCard({
  post,
  index,
}: {
  post: TrendingPost;
  index: number;
}) {
  return (
    <article
      className={cn(
        'flex min-h-72 flex-col rounded-[8px] border border-t-2 bg-background p-5 shadow-sm transition-colors hover:border-primary/30 hover:bg-muted/20 sm:p-6',
        index === 0 && 'border-t-primary/60',
        index === 1 && 'border-t-blue-500/60',
        index === 2 && 'border-t-violet-500/60',
      )}
    >
      <div className="mb-4 flex items-center justify-between">
        <Badge
          variant="outline"
          className={cn(
            'font-mono',
            index === 0 && 'border-primary/30 bg-primary/10 text-primary',
            index === 1 && 'border-blue-500/30 bg-blue-500/10 text-blue-600',
            index === 2 &&
              'border-violet-500/30 bg-violet-500/10 text-violet-600',
          )}
        >
          {post.type}
        </Badge>
        <span className="font-mono text-xs text-muted-foreground">
          {post.time}
        </span>
      </div>
      <div className="space-y-3">
        <div className="text-sm text-muted-foreground">{post.author}</div>
        <h3 className="text-lg leading-7 font-semibold [word-break:keep-all]">
          {post.title}
        </h3>
        <p className="text-sm leading-6 text-muted-foreground">
          {post.description}
        </p>
      </div>
      <div className="mt-5 flex flex-wrap gap-2">
        {post.tags.map((tag) => (
          <Badge key={tag} variant="secondary" className="font-mono">
            {tag}
          </Badge>
        ))}
      </div>
      <div className="mt-auto flex items-center gap-4 pt-5 font-mono text-xs text-muted-foreground">
        <span>▲ {post.score}</span>
        <span className="inline-flex items-center gap-1">
          <ChatCircleIcon className="size-3" />
          {post.comments}
        </span>
        <BookmarkSimpleIcon className="ml-auto size-4" />
      </div>
    </article>
  );
}

function ModeCard({ mode, index }: { mode: ModeItem; index: number }) {
  const icons = [GlobeHemisphereEastIcon, LockKeyIcon, TagIcon] as const;
  const Icon = icons[index] ?? TagIcon;

  return (
    <article className="rounded-[8px] border bg-background p-5 shadow-sm transition-colors hover:border-primary/30 hover:bg-muted/20 sm:p-6">
      <div className="mb-5 flex size-10 items-center justify-center rounded-[6px] bg-primary/10 text-primary">
        <Icon className="size-5" />
      </div>
      <h3 className="text-lg font-semibold [word-break:keep-all]">
        {mode.title}
      </h3>
      <p className="mt-3 text-sm leading-6 text-muted-foreground">
        {mode.description}
      </p>
    </article>
  );
}

function MarketingFooter({
  description,
  columns,
}: {
  description: string;
  columns: FooterColumn[];
}) {
  return (
    <footer id="footer" className="border-b bg-background">
      <div className="mx-auto grid max-w-7xl gap-10 px-4 py-12 sm:px-6 md:grid-cols-[1.1fr_2fr] lg:px-8">
        <div className="space-y-4">
          <Logo />
          <p className="max-w-sm text-sm leading-6 text-muted-foreground">
            {description}
          </p>
        </div>

        <div className="grid grid-cols-2 gap-x-8 gap-y-7 lg:grid-cols-4">
          {columns.map((column) => (
            <div key={column.title}>
              <h3 className="mb-3 text-sm font-semibold">{column.title}</h3>
              <ul className="space-y-2">
                {column.links.map((link) => (
                  <li key={link}>
                    <Link
                      href="#top"
                      className="text-sm text-muted-foreground hover:text-foreground"
                    >
                      {link}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </footer>
  );
}
