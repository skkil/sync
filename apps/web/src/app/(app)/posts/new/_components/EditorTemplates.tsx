'use client';

import { useTranslations } from 'next-intl';

import type { LocalizedTemplate } from '@/features/editor/templates';

interface EditorTemplatesProps {
  templates: LocalizedTemplate[];
  onSelect: (template: LocalizedTemplate) => void;
}

export function EditorTemplates({ templates, onSelect }: EditorTemplatesProps) {
  const t = useTranslations('components.editor');

  return (
    <div className="mt-4 text-muted-foreground">
      {t('templates.suggestions')}

      <div className="flex flex-col gap-2 items-start mt-2">
        {templates.map((template) => (
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
