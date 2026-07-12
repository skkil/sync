// For more info, see https://github.com/storybookjs/eslint-plugin-storybook#configuration-flat-config-format
import nextVitals from 'eslint-config-next/core-web-vitals';
import nextTs from 'eslint-config-next/typescript';
import i18next from 'eslint-plugin-i18next';
import storybook from 'eslint-plugin-storybook';
import { defineConfig, globalIgnores } from 'eslint/config';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
// eslint-plugin-i18next's rule options are shallow-merged with its own
// defaults, so passing e.g. `callees: { exclude: [...] }` silently drops the
// plugin's own default excludes (addEventListener, require, etc.) instead of
// adding to them. Pull the real defaults in and merge by hand.
const i18nextDefaults = require('eslint-plugin-i18next/lib/options/defaults');

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  // Override default ignores of eslint-config-next.
  globalIgnores([
    // Default ignores of eslint-config-next:
    '.next/**',
    'out/**',
    'build/**',
    'next-env.d.ts',
    'src/app/_legacy/**',
  ]),
  {
    rules: {
      // console.log is almost always leftover debugging; warn/error are
      // legitimate for surfacing real failures (error boundaries, parser
      // fallbacks, etc.) so allow those without needing per-line disables.
      'no-console': ['warn', { allow: ['warn', 'error'] }],
    },
  },
  ...(() => {
    // Disallow hardcoded strings so they're forced through next-intl's
    // t()/useTranslations()/getTranslations(). See
    // https://github.com/edvardchen/eslint-plugin-i18next.
    const commonOptions = {
      message:
        'Hardcoded string — wrap it with next-intl t()/useTranslations() instead',
      callees: {
        exclude: [
          ...i18nextDefaults.callees.exclude,
          // next-intl translation functions: t, tAuth, tCommon, ...
          't([A-Z]\\w*)?',
          'useTranslations',
          'getTranslations',
          // className builders — not user-facing text
          'cn',
          'clsx',
          'cva',
          'cx',
          'console\\..*',
          // next-intl rich/markup/raw variants: t.rich(...), tFoo.rich(...)
          't([A-Z]\\w*)?\\.(rich|markup|raw)',
          // technical/library APIs, not user-facing copy
          'isActive', // tiptap editor.isActive('bold')
          'searchParams\\.get', // URLSearchParams
          'searchParams\\.has',
          'register', // react-hook-form form.register('fieldName')
        ],
      },
      'jsx-attributes': {
        exclude: [
          ...i18nextDefaults['jsx-attributes'].exclude,
          'href',
          'rel',
          'target',
          'src', // image/asset paths, not translatable copy
          'variant',
          'size',
          'data-testid',
          'data-slot',
          'name',
          'htmlFor',
          'as',
          // Radix/shadcn technical props, not translatable copy
          'value',
          'defaultValue',
          'orientation',
          'side',
          'align',
          'sortOrder',
          'defaultOpen',
          'data-state',
          'attribute', // next-themes <ThemeProvider attribute="class">
          'defaultTheme', // next-themes <ThemeProvider defaultTheme="system">
          'autoComplete',
          'color', // badge/icon color variants (e.g. Phosphor icon color)
          'weight', // Phosphor icon weight ('fill' | 'regular' | 'bold')
          'answerCode', // <Console> demo code block content in marketing copy
        ],
      },
      words: {
        exclude: [
          ...i18nextDefaults.words.exclude,
          'use client',
          'use server',
          // Brand names — never translated, regardless of where they appear
          'sync',
          'Google',
        ],
      },
      'object-properties': {
        exclude: [
          ...i18nextDefaults['object-properties'].exclude,
          // TanStack Query cache keys, next/font & next/metadata config —
          // technical identifiers, not translatable copy
          'queryKey',
          'mutationKey',
          'variable',
          'subsets',
          'template',
          'default',
          'id',
          'intent',
        ],
      },
    };

    // Technical/library-wrapper layers (shadcn/Radix primitives, Tiptap
    // extension internals, hooks, lib) are dense with framework config
    // strings (data-* attribute values, CSS values, node/command names) that
    // aren't translatable copy. Only flag plain JSX text there.
    const technicalDirs = [
      'src/components/ui/**',
      'src/components/feature/post/editor/extensions/**',
      'src/components/feature/post/editor/primitives/**',
      'src/lib/**',
      'src/hooks/**',
      'src/store/**',
    ];

    const ignores = [
      '**/*.stories.@(js|jsx|ts|tsx)',
      '**/*.test.@(js|jsx|ts|tsx)',
      '**/*.spec.@(js|jsx|ts|tsx)',
      'src/api/__generated__/**',
    ];

    return [
      {
        // Feature/page/app code — this is where real user-facing copy lives.
        // mode: 'jsx-only' checks JSX text AND attribute values (placeholder,
        // aria-label, title, ...), not just bare JSX children. It does NOT
        // reach string literals in detached handler functions/hooks (e.g.
        // toast.error('...') declared outside the returned JSX tree) — that
        // would require mode: 'all', which is far too noisy for this
        // codebase (React Query keys, Radix props, framework config, etc.
        // all show up as literal strings too). Catching those is left to
        // code review.
        files: ['src/**/*.{ts,tsx}'],
        ignores: [...ignores, ...technicalDirs],
        plugins: { i18next },
        rules: {
          'i18next/no-literal-string': [
            'error',
            { ...commonOptions, mode: 'jsx-only' },
          ],
        },
      },
      {
        files: technicalDirs,
        ignores,
        plugins: { i18next },
        rules: {
          'i18next/no-literal-string': [
            'error',
            { ...commonOptions, mode: 'jsx-text-only' },
          ],
        },
      },
    ];
  })(),
  ...storybook.configs['flat/recommended'],
]);

export default eslintConfig;
