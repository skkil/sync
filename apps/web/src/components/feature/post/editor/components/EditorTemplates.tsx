'use client';

import { useTranslations } from 'next-intl';
import DEEP_DIVE from 'public/assets/templates/deep-dive.json';
import PROBLEM_AND_SOLUTION from 'public/assets/templates/problem-and-solution.json';
import TIL from 'public/assets/templates/til.json';
import WEEKLY_REFLECTION from 'public/assets/templates/weekly-reflection.json';

import { useGetProjectPostTemplates } from '@/api/__generated__/post-template/post-template';

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

/** 프로젝트 관리자가 만든 템플릿을 내장 템플릿과 같은 계약으로 매핑한다. 손상된 본문 JSON 은 조용히 거른다. */
function useProjectTemplates(projectHandle?: string): LocalizedTemplate[] {
  const { data } = useGetProjectPostTemplates(projectHandle ?? '', {
    query: { enabled: Boolean(projectHandle), retry: false },
  });

  return (data?.data.templates ?? []).flatMap((template) => {
    try {
      return [
        {
          id: `project-${template.externalId}`,
          label: template.name,
          title: template.title ?? '',
          content: JSON.parse(template.content) as JSON,
        },
      ];
    } catch {
      return [];
    }
  });
}

interface EditorTemplatesProps {
  locale: string;
  /** 프로젝트 글쓰기 컨텍스트라면 그 프로젝트의 템플릿을 내장 템플릿 앞에 함께 보여준다. */
  projectHandle?: string;
  onSelect: (template: LocalizedTemplate) => void;
}

export function EditorTemplates({
  locale,
  projectHandle,
  onSelect,
}: EditorTemplatesProps) {
  const t = useTranslations('components.editor');

  const projectTemplates = useProjectTemplates(projectHandle);
  const templates = [...projectTemplates, ...getTemplates(locale)];

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
