import type { NextConfig } from 'next';
import createNextIntlPlugin from 'next-intl/plugin';

const STAGE = process.env.STAGE;

const nextConfig: NextConfig = {
  rewrites: async () => {
    return [
      {
        source: '/@:handle/:path*',
        destination: '/profile/:handle/:path*',
      },
    ];
  },
  images: {
    remotePatterns: [
      {
        protocol: 'http',
        hostname: 'localhost',
        port: '4566',
        pathname: '/**',
      },
    ],
  },
};

const withNextIntl = createNextIntlPlugin('./src/lib/i18n.ts');
export default withNextIntl(nextConfig);
