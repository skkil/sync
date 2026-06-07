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

export function getTemplates(locale: string): LocalizedTemplate[] {
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

export type { LocalizedTemplate };
