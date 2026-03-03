import type { Translations } from '@/types/i18n';

declare module '@tanstack/react-table' {
  interface TableMeta {
    t: Translations;
  }
}
