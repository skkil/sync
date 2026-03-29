import type { NextConfig } from 'next';
import createNextIntlPlugin from 'next-intl/plugin';

const nextConfig: NextConfig = {
  rewrites: async () => {
    return [
      {
        source: '/@:handle/:path*',
        destination: '/profile/:handle/:path*',
      },
    ];
  },
};

const withNextIntl = createNextIntlPlugin('./src/lib/i18n.ts');
export default withNextIntl(nextConfig);
