import type { Metadata } from 'next';
import { getTranslations } from 'next-intl/server';
import { readFile } from 'node:fs/promises';
import path from 'node:path';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

export async function generateMetadata(): Promise<Metadata> {
  const t = await getTranslations('pages.legal.privacy');

  return { title: t('title') };
}

export default async function Privacy() {
  const content = await readFile(
    path.join(process.cwd(), 'public/legal/privacy.ko.md'),
    'utf-8',
  );

  return (
    <div className="max-w-3xl w-full mx-auto px-6 py-16">
      <article className="prose dark:prose-invert prose-headings:font-medium max-w-none">
        <ReactMarkdown remarkPlugins={[remarkGfm]}>{content}</ReactMarkdown>
      </article>
    </div>
  );
}
