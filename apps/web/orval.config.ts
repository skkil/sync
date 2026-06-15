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
          useInfinite: false,
          useInfiniteQueryParam: 'after',
        },
        mutator: {
          path: './src/lib/server.ts',
          name: 'api',
        },
        namingConvention: {
          enum: 'PascalCase',
        },
        operations: {
          // TODO: Temporary fix for Orval issue where infinite queries are generated for every
          // single operation with pagination parameters, even if the operation is not intended
          // to be used as an infinite query.
          // See https://github.com/orval-labs/orval/issues/3101
          GetPosts: {
            query: {
              useInfinite: true,
            },
          },
          GetUserPosts: {
            query: {
              useInfinite: true,
            },
          },
          SearchProviders: {
            query: {
              useInfinite: true,
            },
          },
          GetMyProviders: {
            query: {
              useInfinite: true,
            },
          },
          GetUnverifiedProviders: {
            query: {
              useInfinite: true,
            },
          },
          GetTeamBuildingPosts: {
            query: {
              useInfinite: true,
            },
          },
          GetTeamBuildingPostsByProject: {
            query: {
              useInfinite: true,
            },
          },
          GetTrendingProjects: {
            query: {
              useInfinite: true,
            },
          },
          GetRecentFeed: {
            query: {
              useInfinite: true,
            },
          },
          GetBookmarkedPosts: {
            query: {
              useInfinite: true,
            },
          },
          GetPostsByProject: {
            query: {
              useInfinite: true,
            },
          },
        },
      },
    },
  },
});
