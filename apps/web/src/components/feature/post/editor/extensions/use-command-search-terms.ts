import { useTranslations } from 'next-intl';
import { useMemo } from 'react';

import { COMMAND_NAMES, type CommandSearchTerms } from './commands';

/** 슬래시 메뉴 검색어(명령 제목 + 키워드)를 i18n 문자열로 만든다. 글 에디터와 템플릿 에디터가 공유한다. */
export function useCommandSearchTerms(): CommandSearchTerms {
  const t = useTranslations('components.editor');

  return useMemo(
    () =>
      Object.fromEntries(
        COMMAND_NAMES.map((name) => [
          name,
          [
            t(`commands.${name}.title`),
            ...t(`commands.${name}.keywords`).split(/\s+/),
          ],
        ]),
      ),
    [t],
  );
}
