import { getTranslations } from 'next-intl/server';

const FAQ_KEYS = ['vsBlog', 'project', 'privacy', 'personalVsProject'] as const;

/** 4. FAQ. */
export default async function FaqSection() {
  const t = await getTranslations('pages.about.faq');

  return (
    <section className="border-y border-border bg-muted/30">
      <div className="mx-auto w-full max-w-3xl px-6 py-20">
        <h2 className="text-2xl font-medium tracking-tight md:text-3xl">
          {t('title')}
        </h2>

        <div className="mt-10 divide-y divide-border border-y border-border">
          {FAQ_KEYS.map((key) => (
            <FaqItem
              key={key}
              question={t(`items.${key}.question`)}
              answer={t(`items.${key}.answer`)}
            />
          ))}
        </div>
      </div>
    </section>
  );
}

function FaqItem({ question, answer }: { question: string; answer: string }) {
  return (
    <details className="group py-4">
      <summary className="flex cursor-pointer list-none items-center justify-between gap-4 text-[15px] font-medium marker:hidden">
        {question}
        <svg
          viewBox="0 0 24 24"
          className="size-4 shrink-0 text-muted-foreground transition-transform group-open:rotate-45"
          fill="none"
          aria-hidden="true"
        >
          <path
            d="M12 5v14M5 12h14"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </summary>
      <p className="mt-3 max-w-2xl text-sm leading-relaxed text-muted-foreground">
        {answer}
      </p>
    </details>
  );
}
