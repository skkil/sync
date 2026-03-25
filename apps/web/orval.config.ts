import { defineConfig } from 'orval';

export default defineConfig({
  api: {
    input: {
      target: '../../docs/api/openapi3.yaml',
    },
    output: {
      mode: 'tags-split',
      target: './src/api/__generated__',
      schemas: './src/api/__generated__/types',
      namingConvention: 'PascalCase',
      client: 'react-query',
      prettier: true,
      override: {
        query: {
          useInfinite: true,
          useInfiniteQueryParam: 'cursor',
        },
        mutator: {
          path: './src/lib/server.ts',
          name: 'api',
        },
        namingConvention: {
          enum: 'PascalCase',
        },
      },
    },
  },
});
