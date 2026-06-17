'use client';

import { useTranslations } from 'next-intl';
import DEEP_DIVE from 'public/assets/templates/deep-dive.json';
import PROBLEM_AND_SOLUTION from 'public/assets/templates/problem-and-solution.json';
import TIL from 'public/assets/templates/til.json';
import WEEKLY_REFLECTION from 'public/assets/templates/weekly-reflection.json';

interface Template {
  id: string;
  label: {
    [locale: string]: string;
  };
  title: {
    [locale: string]: string;
  };
  content: {
    [locale: string]: string;
  };
}

interface LocalizedTemplate {
  id: string;
  label: string;
  title: string;
  content: JSON;
}

const ALL_TEMPLATES: Template[] = [
  TIL,
  DEEP_DIVE,
  PROBLEM_AND_SOLUTION,
  WEEKLY_REFLECTION,
];

function getTemplates(locale: string): LocalizedTemplate[] {
  return ALL_TEMPLATES.filter(
    (template) =>
      template.label[locale] &&
      template.title[locale] &&
      template.content[locale],
  ).map((template) => ({
    id: template.id,
    label: template.label[locale]!,
    title: template.title[locale]!,
    content: JSON.parse(template.content[locale]!) as JSON,
  }));
}

interface EditorTemplatesProps {
  locale: string;
  onSelect: (template: LocalizedTemplate) => void;
}

export function EditorTemplates({ locale, onSelect }: EditorTemplatesProps) {
  const t = useTranslations('components.editor');

  return (
    <div className="mt-4 text-muted-foreground">
      {t('templates.suggestions')}

      <div className="flex flex-col gap-2 items-start mt-2">
        {getTemplates(locale).map((template) => (
          <button
            key={template.id}
            type="button"
            className="px-3 py-1.5 text-sm text-muted-foreground hover:underline"
            onClick={() => onSelect(template)}
          >
            {template.label}
          </button>
        ))}
      </div>
    </div>
  );
}
