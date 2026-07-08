import { getTranslations } from 'next-intl/server';
import Link from 'next/link';

import { Button, LinkButton } from '@/components/ui/button';
import { Copyright } from '@/components/ui/copyright';
import { Logo } from '@/components/ui/logo';
import ROUTES from '@/util/routes';

export default async function About() {
  const t = await getTranslations('pages.about');

  // TODO: 랜딩 페이지 고도화

  return (
    <div className="w-full min-h-screen flex flex-col">
      <header className="flex items-center justify-between px-6 py-4 max-w-5xl w-full mx-auto">
        <Logo />

        <div className="flex items-center gap-2">
          <LinkButton href={ROUTES.LOGIN()} variant="ghost">
            {t('nav.login')}
          </LinkButton>
          <LinkButton href={ROUTES.REGISTER()}>{t('nav.register')}</LinkButton>
        </div>
      </header>

      <main className="flex-1">
        <section className="relative overflow-hidden px-6 pt-20 pb-24 text-center">
          <div
            className="absolute inset-0 -z-10 opacity-[0.07]"
            style={{
              backgroundImage:
                'radial-gradient(circle, currentColor 1.5px, transparent 1.5px)',
              backgroundSize: '28px 28px',
            }}
          />

          <h1 className="text-4xl md:text-5xl font-light mb-4 max-w-2xl mx-auto">
            {t('hero.title')}
          </h1>

          <p className="text-muted-foreground max-w-lg mx-auto mb-8">
            {t('hero.description')}
          </p>

          <div className="flex flex-col items-center justify-center gap-3 sm:flex-row">
            <Button asChild>
              <Link href={ROUTES.EXPLORE()}>{t('hero.actions.explore')}</Link>
            </Button>
            <Button variant="outline" asChild>
              <Link href={ROUTES.REGISTER()}>{t('hero.actions.register')}</Link>
            </Button>
          </div>
        </section>
      </main>

      <footer className="px-6 py-12">
        <div className="max-w-5xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-8">
          <div className="col-span-2 md:col-span-1">
            <Logo />
            <p className="text-sm text-muted-foreground mt-3 max-w-[220px]">
              {t('footer.description')}
            </p>
          </div>

          <div>
            <h4 className="text-sm font-medium mb-3">
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
        </div>

        <div className="max-w-5xl mx-auto mt-10 pt-6 border-t">
          <Copyright />
        </div>
      </footer>
    </div>
  );
}
